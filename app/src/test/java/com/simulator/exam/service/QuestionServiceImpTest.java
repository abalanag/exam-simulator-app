package com.simulator.exam.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import com.simulator.exam.entity.Question;
import com.simulator.exam.exception.DuplicateQuestionException;
import com.simulator.exam.exception.ModuleNotFoundException;
import com.simulator.exam.repository.QuestionRepository;
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
    @SuppressWarnings("unused")
    private AnswerService answerService;

    @Mock
    private MultipartFile mockMultipartFile;

    @InjectMocks
    private QuestionServiceImp questionService;

    private List<Question> questionList;
    private List<QuestionsStructureDo> structureList;

    private List<QuestionsStructureDo> structureListWithNullModule;

    private List<QuestionsStructureDo> structureListWithMissingModule;

    private static final String MODULE_NAME = "SPRING_AOP";

    private static final String MISSING_MODULE_NAME = "MISSING_MODULE";

    private static final String NULL_MODULE = null;

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
        questionList.add(new Question("description1", Collections.emptyList(), MODULE_NAME));
        questionList.add(new Question("description2", Collections.emptyList(), MODULE_NAME));

        structureList = List.of(new QuestionsStructureDo(MODULE_NAME, 2));
        structureListWithNullModule = List.of(new QuestionsStructureDo(NULL_MODULE, 2));
        structureListWithMissingModule = List.of(new QuestionsStructureDo(MISSING_MODULE_NAME, 2));

        questionService.setFilePath("src/test/resources/questions-files/%s");
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
        when(questionRepository.getTopByModuleEnumQuestionOrderByRandom(2, MODULE_NAME)).thenReturn(questionList);
        when(questionRepository.moduleHasNoQuestions(MODULE_NAME)).thenReturn(Boolean.FALSE);

        final List<QuestionDo> result = questionService.getRandomQuestionsByModule(MODULE_NAME, 2);

        assertEquals(2, result.size());
        verify(questionRepository, times(1)).getTopByModuleEnumQuestionOrderByRandom(2, MODULE_NAME);
    }

    @Test
    void testGetQuestionsByStructure() {
        when(questionRepository.getTopByModuleEnumQuestionOrderByRandom(2, MODULE_NAME)).thenReturn(questionList);
        when(questionRepository.moduleHasNoQuestions(MODULE_NAME)).thenReturn(Boolean.FALSE);

        final List<QuestionDo> result = questionService.getQuestionsByStructure(structureList);

        assertEquals(2, result.size());
    }

    @Test
    void testGetQuestionsByStructureWithNullModule() {
        assertThrows(ModuleNotFoundException.class,
                () -> questionService.getQuestionsByStructure(structureListWithNullModule));
    }

    @Test
    void testGetQuestionsByStructureWithMissingModule() {
        when(questionRepository.moduleHasNoQuestions(MISSING_MODULE_NAME)).thenReturn(Boolean.TRUE);

        assertThrows(ModuleNotFoundException.class,
                () -> questionService.getQuestionsByStructure(structureListWithMissingModule));

    }

    @Test
    void testSaveImportLocalQuestionByModuleNumber() {
        when(questionRepository.isQuestionAlreadySaved(any())).thenReturn(Boolean.FALSE);

        questionService.saveImportLocalQuestionByFileName(FILE_NAME, MODULE_NAME);

        verify(questionRepository, times(2)).save(any(Question.class));
    }

    @Test
    void testSaveImportQuestionsFromFile() throws IOException {
        when(mockMultipartFile.getInputStream()).thenReturn(
                new MockMultipartFile("file", validYamlData.getBytes()).getInputStream());

        when(questionRepository.isQuestionAlreadySaved(any())).thenReturn(Boolean.FALSE);

        questionService.saveImportQuestionsFromFile(mockMultipartFile, MODULE_NAME);

        verify(questionRepository, times(2)).save(any(Question.class));
    }

    @Test
    void testSaveUniqueQuestionThroughSaveImportLocalQuestionByModuleNumber() {

        final Question uniqueQuestion = new Question("uniqueDescription", Collections.emptyList(), MODULE_NAME);
        questionList.add(uniqueQuestion);

        when(questionRepository.isQuestionAlreadySaved(any())).thenReturn(Boolean.FALSE);
        questionService.saveImportLocalQuestionByFileName(FILE_NAME, MODULE_NAME);

        verify(questionRepository, times(2)).save(any());
    }

    @Test
    void testSaveDuplicateQuestionThroughSaveImportLocalQuestionByModuleNumber() {
        final String fileName = "test-duplicated-questions.yaml";
        final Question duplicateQuestion = new Question("description1", Collections.emptyList(), MODULE_NAME);
        questionList.add(duplicateQuestion);

        when(questionRepository.isQuestionAlreadySaved(any())).thenReturn(Boolean.TRUE);

        assertThrows(DuplicateQuestionException.class,
                () -> questionService.saveImportLocalQuestionByFileName(fileName, MODULE_NAME));

        verify(questionRepository, never()).save(any());
    }

    @Test
    void testSaveUniqueQuestionThroughSaveImportQuestionsFromFile() throws IOException {

        final Question uniqueQuestion = new Question("uniqueDescription", Collections.emptyList(), MODULE_NAME);
        questionList.add(uniqueQuestion);

        when(mockMultipartFile.getInputStream()).thenReturn(
                new MockMultipartFile("file", validYamlData.getBytes()).getInputStream());
        when(questionRepository.isQuestionAlreadySaved(any())).thenReturn(Boolean.FALSE);

        questionService.saveImportQuestionsFromFile(mockMultipartFile, MODULE_NAME);

        verify(questionRepository, times(2)).save(any(Question.class));
    }

    @Test
    void testSaveDuplicateQuestionThroughSaveImportQuestionsFromFile() throws IOException {

        final Question duplicateQuestion = new Question("description1", Collections.emptyList(), MODULE_NAME);
        questionList.add(duplicateQuestion);

        when(mockMultipartFile.getInputStream()).thenReturn(
                new MockMultipartFile("file", invalidYaml.getBytes()).getInputStream());
        when(questionRepository.isQuestionAlreadySaved(any())).thenReturn(Boolean.TRUE);

        assertThrows(DuplicateQuestionException.class,
                () -> questionService.saveImportQuestionsFromFile(mockMultipartFile, MODULE_NAME));

        verify(questionRepository, never()).save(any(Question.class));
    }
}