package com.simulator.exam.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.simulator.exam.dto.QuestionDo;
import com.simulator.exam.dto.QuestionsStructureDo;
import com.simulator.exam.entity.ModuleEnum;
import com.simulator.exam.entity.Question;
import com.simulator.exam.exception.DuplicateQuestionException;
import com.simulator.exam.repository.QuestionRepository;
import com.simulator.exam.util.ExamUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class QuestionServiceImpTest {

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private AnswerService answerService;

    @Mock
    private MultipartFile mockMultipartFile;

    @InjectMocks
    private QuestionServiceImp questionService;

    private List<Question> questionList;
    private List<QuestionsStructureDo> structureList;

    private static final ModuleEnum MODULE = ModuleEnum.SPRING_AOP;

    private static final Integer MODULE_NUMBER = 2;

    private final static String FILE_NAME = "test-questions.yaml";

    private final String validYamlData = """
            questions:
              - description: "What is the capital of Canada?"
                answers:
                  - option: "Toronto"
                    correct: true
                  - option: "Ottawa"
                    correct: false
                  - option: "Vancouver"
                    correct: false
              - description: "What is 2 + 2?"
                answers:
                  - option: "3"
                    correct: false
                  - option: "4"
                    correct: true
                  - option: "5"
                    correct: false""";

    private final String invalidYaml = """
            questions:
              - description: "description1"
                answers:
                  - option: "Toronto"
                    correct: true""";

    @BeforeEach
    void setUp() {
        questionList = new ArrayList<>();
        questionList.add(new Question("description1", Collections.emptyList(), MODULE));
        questionList.add(new Question("description2", Collections.emptyList(), MODULE));

        structureList = new ArrayList<>();
        structureList.add(new QuestionsStructureDo(MODULE.name(), 2));

        ExamUtils.setFilePath("src/test/resources/questions-files/%s");
    }

    @Test
    void testGetRandomQuestions() {
        when(questionRepository.getQuestionsOrderByRandom()).thenReturn(questionList);

        final List<QuestionDo> result = questionService.getRandomQuestions();

        assertEquals(2, result.size());
        verify(questionRepository, times(1)).getQuestionsOrderByRandom();
    }

    @Test
    void testGetRandomQuestionsByModule() {
        when(questionRepository.getTopByModuleEnumQuestionOrderByRandom(2, MODULE.name())).thenReturn(questionList);

        final List<QuestionDo> result = questionService.getRandomQuestionsByModule(MODULE.name(), 2);

        assertEquals(2, result.size());
        verify(questionRepository, times(1)).getTopByModuleEnumQuestionOrderByRandom(2, MODULE.name());
    }

    @Test
    void testGetQuestionsByStructure() {
        when(questionRepository.getTopByModuleEnumQuestionOrderByRandom(2, MODULE.name())).thenReturn(questionList);

        final List<QuestionDo> result = questionService.getQuestionsByStructure(structureList);

        assertEquals(2, result.size());
    }

    @Test
    void testSaveImportLocalQuestionByModuleNumber() {
        when(questionRepository.findAll()).thenReturn(Collections.emptyList());

        questionService.saveImportLocalQuestionByModuleNumber(FILE_NAME, MODULE_NUMBER);

        verify(questionRepository, times(2)).save(any(Question.class));
        verify(answerService, times(1)).saveAnswers(anyList(), anyList());
    }

    @Test
    void testSaveImportQuestionsFromFile() throws IOException {
        when(mockMultipartFile.getInputStream()).thenReturn(
                new MockMultipartFile("file", validYamlData.getBytes()).getInputStream());
        when(questionRepository.findAll()).thenReturn(Collections.emptyList());

        questionService.saveImportQuestionsFromFile(mockMultipartFile, MODULE_NUMBER);

        verify(questionRepository, times(2)).save(any(Question.class));
        verify(answerService, times(1)).saveAnswers(anyList(), anyList());
    }

    @Test
    void testSaveUniqueQuestionThroughSaveImportLocalQuestionByModuleNumber() {

        final Question uniqueQuestion = new Question("uniqueDescription", Collections.emptyList(), MODULE);
        questionList.add(uniqueQuestion);

        when(questionRepository.findAll()).thenReturn(questionList.subList(0, 2));

        questionService.saveImportLocalQuestionByModuleNumber(FILE_NAME, MODULE_NUMBER);

        verify(questionRepository, times(2)).save(any());
    }

    @Test
    void testSaveDuplicateQuestionThroughSaveImportLocalQuestionByModuleNumber() {
        final String fileName = "test-duplicated-questions.yaml";
        final Question duplicateQuestion = new Question("description1", Collections.emptyList(), MODULE);
        questionList.add(duplicateQuestion);

        when(questionRepository.findAll()).thenReturn(questionList.subList(0, 2));

        assertThrows(DuplicateQuestionException.class,
                () -> questionService.saveImportLocalQuestionByModuleNumber(fileName, MODULE_NUMBER));

        verify(questionRepository, never()).save(any());
    }

    @Test
    void testSaveUniqueQuestionThroughSaveImportQuestionsFromFile() throws IOException {

        final Question uniqueQuestion = new Question("uniqueDescription", Collections.emptyList(), MODULE);
        questionList.add(uniqueQuestion);

        when(mockMultipartFile.getInputStream()).thenReturn(
                new MockMultipartFile("file", validYamlData.getBytes()).getInputStream());
        when(questionRepository.findAll()).thenReturn(questionList.subList(0, 2));

        questionService.saveImportQuestionsFromFile(mockMultipartFile, MODULE_NUMBER);

        verify(questionRepository, times(2)).save(any(Question.class));
    }

    @Test
    void testSaveDuplicateQuestionThroughSaveImportQuestionsFromFile() throws IOException {

        final Question duplicateQuestion = new Question("description1", Collections.emptyList(), MODULE);
        questionList.add(duplicateQuestion);

        when(mockMultipartFile.getInputStream()).thenReturn(
                new MockMultipartFile("file", invalidYaml.getBytes()).getInputStream());
        when(questionRepository.findAll()).thenReturn(questionList.subList(0, 2));

        assertThrows(DuplicateQuestionException.class,
                () -> questionService.saveImportQuestionsFromFile(mockMultipartFile, MODULE_NUMBER));

        verify(questionRepository, never()).save(any(Question.class));
    }
}