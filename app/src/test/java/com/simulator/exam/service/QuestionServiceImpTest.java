package com.simulator.exam.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.simulator.exam.dto.AnswerDo;
import com.simulator.exam.dto.QuestionDo;
import com.simulator.exam.dto.QuestionsStructureDo;
import com.simulator.exam.entity.Answer;
import com.simulator.exam.entity.Question;
import com.simulator.exam.exception.DuplicateQuestionException;
import com.simulator.exam.exception.ModuleNotFoundException;
import com.simulator.exam.repository.QuestionRepository;
import jakarta.persistence.EntityNotFoundException;
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

    private List<Question> questionListWithEmptyAnswer;

    final Question question = new Question();
    final Question newQuestion = new Question();

    private Question questionWithAnswerList;

    private List<QuestionsStructureDo> structureList;

    private List<QuestionsStructureDo> structureListWithNullModule;

    private List<QuestionsStructureDo> structureListWithMissingModule;

    private static final List<Answer> ANSWER_EMPTY_LIST = List.of();
    private List<Answer> answerList;
    private List<Answer> newAnswerList;
    private List<AnswerDo> newAnswerDoList;

    private List<AnswerDo> answerDoList;

    private static final String MODULE_NAME = "SPRING_AOP";
    private static final String NULL_MODULE = null;

    private static final String MISSING_MODULE_NAME = "MISSING_MODULE";

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
        questionListWithEmptyAnswer = new ArrayList<>();
        questionListWithEmptyAnswer.add(new Question(1L, "description1", MODULE_NAME, Collections.emptyList()));
        questionListWithEmptyAnswer.add(new Question(2L, "description2", MODULE_NAME, Collections.emptyList()));

        structureList = List.of(new QuestionsStructureDo(MODULE_NAME, 2));
        structureListWithNullModule = List.of(new QuestionsStructureDo(NULL_MODULE, 2));
        structureListWithMissingModule = List.of(new QuestionsStructureDo(MISSING_MODULE_NAME, 2));

        questionService.setFilePath("src/test/resources/questions-files/%s");

        answerList = new ArrayList<>();
        answerList.add(new Answer(1L, "Is true", true));
        answerList.add(new Answer(1L, "Is false", false));

        newAnswerList = new ArrayList<>();
        newAnswerList.add(new Answer(1L, "The answer is new", true));
        newAnswerList.add(new Answer(1L, "The answer is not new", false));

        answerDoList = new ArrayList<>();
        answerDoList.add(new AnswerDo(1L, "Is true", true));
        answerDoList.add(new AnswerDo(1L, "Is false", false));

        newAnswerDoList = new ArrayList<>();
        newAnswerDoList.add(new AnswerDo(1L, "The answer is new", true));
        newAnswerDoList.add(new AnswerDo(1L, "The answer is not new", false));

        questionWithAnswerList = new Question(1L, "Is question correct?", "A module", answerList);
    }

    @Test
    void testGetRandomQuestions() {
        when(questionRepository.getQuestionsOrderByRandom()).thenReturn(questionListWithEmptyAnswer);

        final List<QuestionDo> result = questionService.getRandomQuestions();

        assertEquals(2, result.size());
        verify(questionRepository, times(1)).getQuestionsOrderByRandom();
    }

    @Test
    void testGetRandomQuestionsByModule() {
        when(questionRepository.getTopByModuleEnumQuestionOrderByRandom(2, MODULE_NAME)).thenReturn(
                questionListWithEmptyAnswer);
        when(questionRepository.moduleHasNoQuestions(MODULE_NAME)).thenReturn(Boolean.FALSE);

        final List<QuestionDo> result = questionService.getRandomQuestionsByModule(MODULE_NAME, 2);

        assertEquals(2, result.size());
        verify(questionRepository, times(1)).getTopByModuleEnumQuestionOrderByRandom(2, MODULE_NAME);
    }

    @Test
    void testGetRandomQuestionsByModuleWithNullOREmptyModule() {
        assertThrows(ModuleNotFoundException.class, () -> questionService.getRandomQuestionsByModule(NULL_MODULE, 2));
        assertThrows(ModuleNotFoundException.class, () -> questionService.getRandomQuestionsByModule("", 2));
    }

    @Test
    void testGetRandomQuestionsByModuleWithNoQuestionsForSpecifiedModule() {
        when(questionRepository.moduleHasNoQuestions(MODULE_NAME)).thenReturn(Boolean.TRUE);
        assertThrows(ModuleNotFoundException.class, () -> questionService.getRandomQuestionsByModule("SPRING_AOP", 2));
    }

    @Test
    void testGetQuestionsByStructure() {
        when(questionRepository.getTopByModuleEnumQuestionOrderByRandom(2, MODULE_NAME)).thenReturn(
                questionListWithEmptyAnswer);
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
        when(questionRepository.isQuestionAlreadySaved(any())).thenReturn(Boolean.FALSE);
        questionService.saveImportLocalQuestionByFileName(FILE_NAME, MODULE_NAME);

        verify(questionRepository, times(2)).save(any());
    }

    @Test
    void testSaveDuplicateQuestionThroughSaveImportLocalQuestionByModuleNumber() {
        final String fileName = "test-duplicated-questions.yaml";

        when(questionRepository.isQuestionAlreadySaved(any())).thenReturn(Boolean.TRUE);

        assertThrows(DuplicateQuestionException.class,
                () -> questionService.saveImportLocalQuestionByFileName(fileName, MODULE_NAME));

        verify(questionRepository, never()).save(any());
    }

    @Test
    void testSaveUniqueQuestionThroughSaveImportQuestionsFromFile() throws IOException {
        when(mockMultipartFile.getInputStream()).thenReturn(
                new MockMultipartFile("file", validYamlData.getBytes()).getInputStream());
        when(questionRepository.isQuestionAlreadySaved(any())).thenReturn(Boolean.FALSE);

        questionService.saveImportQuestionsFromFile(mockMultipartFile, MODULE_NAME);

        verify(questionRepository, times(2)).save(any(Question.class));
    }

    @Test
    void testSaveDuplicateQuestionThroughSaveImportQuestionsFromFile() throws IOException {
        when(mockMultipartFile.getInputStream()).thenReturn(
                new MockMultipartFile("file", invalidYaml.getBytes()).getInputStream());
        when(questionRepository.isQuestionAlreadySaved(any())).thenReturn(Boolean.TRUE);

        assertThrows(DuplicateQuestionException.class,
                () -> questionService.saveImportQuestionsFromFile(mockMultipartFile, MODULE_NAME));

        verify(questionRepository, never()).save(any(Question.class));
    }

    @Test
    void testGetAllQuestionsWithNoValueReturned() {
        when(questionRepository.findAll()).thenReturn(List.of());
        assertTrue(questionService.getAllQuestions().isEmpty());
    }

    @Test
    void testGetAllQuestionsWithResults() {
        when(questionRepository.findAll()).thenReturn(questionListWithEmptyAnswer);
        assertEquals(2, questionService.getAllQuestions().size());
    }

    @Test
    void testGetQuestionByIdWithNoResult() {
        assertThrows(EntityNotFoundException.class, () -> questionService.getQuestionById(1L));
    }

    @Test
    void testGetQuestionByIdWithResult() {
        when(questionRepository.findById(1L)).thenReturn(Optional.of(questionListWithEmptyAnswer.get(0)));
        assertTrue(
                isQuestionEqualWithQuestionDo(questionListWithEmptyAnswer.get(0), questionService.getQuestionById(1L)));
    }

    @Test
    void testGetAnswersForQuestionId() {
        when(questionRepository.findById(1L)).thenReturn(Optional.of(questionWithAnswerList));
        when(answerService.mapAnswersToAnswerDo(answerList)).thenReturn(answerDoList);

        assertTrue(isAnswerEqualWithAnswerDo(answerList, questionService.getAnswersForQuestionId(1L)));
    }

    @Test
    void testGetAnswersForQuestionIdWithMissingQuestion() {
        assertThrows(EntityNotFoundException.class, () -> questionService.getAnswersForQuestionId(1L));
    }

    @Test
    void testSaveQuestionsNoDuplicateQuestion() {
        when(questionRepository.isQuestionAlreadySaved(anyString())).thenReturn(false);

        questionService.saveQuestions(questionListWithEmptyAnswer);

        verify(questionRepository, times(questionListWithEmptyAnswer.size())).save(any(Question.class));
    }

    @Test
    void testSaveQuestionsWithDuplicateQuestion() {
        when(questionRepository.isQuestionAlreadySaved(anyString())).thenReturn(true);

        assertThrows(DuplicateQuestionException.class,
                () -> questionService.saveQuestions(questionListWithEmptyAnswer));
    }

    @Test
    void testSaveAnswersForTheGivenQuestionSaved() {
        final Long questionId = 1L;
        question.setId(questionId);
        final List<Answer> answers = List.of(new Answer(), new Answer());

        when(questionRepository.findById(questionId)).thenReturn(Optional.of(question));

        questionService.saveAnswersForThGivenQuestion(questionId, answers);
        assertEquals(answers, question.getAnswers());
    }

    @Test
    void testSaveAnswersForTheGivenQuestionWithUnknownQuestionId() {
        assertThrows(EntityNotFoundException.class,
                () -> questionService.saveAnswersForThGivenQuestion(1L, ANSWER_EMPTY_LIST));
    }

    @Test
    void testDeleteGivenQuestion() {
        final List<Question> questionsToDelete = List.of(new Question(), new Question());

        questionService.deleteGivenQuestion(questionsToDelete);

        verify(questionRepository).deleteAll(questionsToDelete);
    }

    @Test
    void testDeleteAnswersForGivenQuestion() {
        final Long questionId = 1L;
        question.setId(questionId);
        question.setAnswers(new ArrayList<>());

        when(questionRepository.findById(questionId)).thenReturn(Optional.of(question));

        questionService.deleteAnswersForGivenQuestion(questionId);

        assertTrue(question.getAnswers().isEmpty());
    }

    @Test
    void testDeleteAnswersForGivenQuestionWithWrongQuestionId() {
        assertThrows(EntityNotFoundException.class, () -> questionService.deleteAnswersForGivenQuestion(1L));
    }

    @Test
    void testDeleteQuestionById() {
        final long questionId = 1L;

        questionService.deleteQuestionById(questionId);

        verify(questionRepository).deleteById(questionId);
    }

    @Test
    void testUpdateQuestion_UpdatesQuestionsInDatabase() {
        when(questionRepository.findAllById(any())).thenReturn(questionListWithEmptyAnswer);

        final List<Question> updatedQuestions =
                List.of(new Question(1L, "New Description 1", "Module A NEW", ANSWER_EMPTY_LIST),
                        new Question(2L, "New Description 2", "Module B NEW", ANSWER_EMPTY_LIST));

        final List<QuestionDo> result = questionService.updateQuestion(updatedQuestions);

        assertEquals("New Description 1", result.get(0).getDescription());
        assertEquals("Module A NEW", result.get(0).getModuleName());
        assertEquals("New Description 2", result.get(1).getDescription());
        assertEquals("Module B NEW", result.get(1).getModuleName());
    }

    @Test
    void testUpdateQuestionWithWrongQuestionId() {
        assertEquals(0, questionService.updateQuestion(questionListWithEmptyAnswer).size());
    }

    @Test
    void testUpdateQuestionWithWrongQuestionId2() {
        newQuestion.setDescription("New Description");
        newQuestion.setAnswers(answerList);
        final List<Question> listOfNewQuestions = List.of(newQuestion);

        assertThrows(EntityNotFoundException.class, () -> questionService.updateQuestion(listOfNewQuestions));
    }

    @Test
    void testUpdateQuestionAnswersByQuestionId() {
        when(questionRepository.findById(1L)).thenReturn(Optional.of(questionWithAnswerList));

        questionService.updateQuestionAnswersByQuestionId(List.of(answerList.get(0), answerList.get(1)), 1L);

        verify(answerService, times(1)).mapAnswersToAnswerDo(List.of(answerList.get(0), answerList.get(1)));
    }

    @Test
    void testUpdateQuestionAnswersByQuestionIdWithWrongQuestionId() {
        assertThrows(EntityNotFoundException.class,
                () -> questionService.updateQuestionAnswersByQuestionId(ANSWER_EMPTY_LIST, 1L));
    }

    @Test
    void testUpdateQuestionByIdWithWrongId() {
        final Question question = new Question();
        assertThrows(EntityNotFoundException.class, () -> questionService.updateQuestionById(question, 1L));
    }

    @Test
    void testUpdateQuestionById() {
        final Question newQuestion = new Question("Is this description new?", newAnswerList, "A new module");

        when(questionRepository.findById(1L)).thenReturn(Optional.of(questionWithAnswerList));
        when(answerService.mapAnswersToAnswerDo(newAnswerList)).thenReturn(newAnswerDoList);

        final QuestionDo result = questionService.updateQuestionById(newQuestion, 1L);

        assertEquals(result.getDescription(), questionWithAnswerList.getDescription());
        assertEquals(result.getModuleName(), questionWithAnswerList.getModuleName());
        assertEquals(result.getId(), questionWithAnswerList.getId());
        assertEquals(result.getAnswers().size(), questionWithAnswerList.getAnswers().size());
        assertEquals(result.getAnswers().get(0).getOption(), questionWithAnswerList.getAnswers().get(0).getOption());
        assertEquals(result.getAnswers().get(0).isCorrect(), questionWithAnswerList.getAnswers().get(0).isCorrect());
    }

    @Test
    void testUpdatedQuestionPropertiesByIdWithWrongId() {
        final Question question = new Question();
        assertThrows(EntityNotFoundException.class, () -> questionService.updatedQuestionPropertiesById(1L, question));
    }

    @Test
    void testUpdatedQuestionPropertiesByIdWithUpdatedDescription() {
        newQuestion.setDescription("New Description");

        when(answerService.mapAnswersToAnswerDo(answerList)).thenReturn(answerDoList);
        when(questionRepository.findById(1L)).thenReturn(Optional.of(questionWithAnswerList));

        final QuestionDo questionDo = questionService.updatedQuestionPropertiesById(1L, newQuestion);
        assertEquals(questionDo.getId(), questionWithAnswerList.getId());
        assertEquals(questionDo.getModuleName(), questionWithAnswerList.getModuleName());
        assertEquals(questionDo.getAnswers().size(), questionWithAnswerList.getAnswers().size());
        assertEquals(questionDo.getAnswers().get(0).getOption(),
                questionWithAnswerList.getAnswers().get(0).getOption());
        assertEquals(questionDo.getAnswers().get(0).isCorrect(),
                questionWithAnswerList.getAnswers().get(0).isCorrect());
        assertEquals(questionDo.getDescription(), newQuestion.getDescription());
    }

    @Test
    void testUpdatedQuestionPropertiesByIdWithUpdatedModule() {
        newQuestion.setModuleName("New Module");

        when(answerService.mapAnswersToAnswerDo(answerList)).thenReturn(answerDoList);
        when(questionRepository.findById(1L)).thenReturn(Optional.of(questionWithAnswerList));

        final QuestionDo questionDo = questionService.updatedQuestionPropertiesById(1L, newQuestion);
        assertEquals(questionDo.getId(), questionWithAnswerList.getId());
        assertEquals(questionDo.getModuleName(), newQuestion.getModuleName());
        assertEquals(questionDo.getAnswers().size(), questionWithAnswerList.getAnswers().size());
        assertEquals(questionDo.getAnswers().get(0).getOption(),
                questionWithAnswerList.getAnswers().get(0).getOption());
        assertEquals(questionDo.getAnswers().get(0).isCorrect(),
                questionWithAnswerList.getAnswers().get(0).isCorrect());
        assertEquals(questionDo.getDescription(), questionWithAnswerList.getDescription());
    }

    @Test
    void testUpdatedQuestionPropertiesByIdWithUpdatedAnswers() {
        newQuestion.setAnswers(newAnswerList);

        when(answerService.mapAnswersToAnswerDo(newAnswerList)).thenReturn(newAnswerDoList);
        when(questionRepository.findById(1L)).thenReturn(Optional.of(questionWithAnswerList));

        final QuestionDo questionDo = questionService.updatedQuestionPropertiesById(1L, newQuestion);
        assertEquals(questionDo.getId(), questionWithAnswerList.getId());
        assertEquals(questionDo.getModuleName(), questionWithAnswerList.getModuleName());
        assertEquals(questionDo.getAnswers().size(), newQuestion.getAnswers().size());
        assertEquals(questionDo.getAnswers().get(0).getOption(), newQuestion.getAnswers().get(0).getOption());
        assertEquals(questionDo.getAnswers().get(0).isCorrect(), newQuestion.getAnswers().get(0).isCorrect());
        assertEquals(questionDo.getDescription(), questionWithAnswerList.getDescription());
    }

    @Test
    void testUpdatedQuestionsPropertiesByIdWithMissingNewQuestionId() {
        newQuestion.setDescription("New Description");
        newQuestion.setAnswers(answerList);
        final List<Question> listOfNewQuestions = List.of(newQuestion);

        when(questionRepository.findAllById(any())).thenReturn(List.of(questionWithAnswerList));

        final EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> questionService.updatedQuestionsPropertiesById(listOfNewQuestions));

        assertEquals("Missing id for provided question: Question{id=null, description='New Description', "
                + "moduleName='null', answers=[Answer{id=1, option='Is true', correct=true}, "
                + "Answer{id=1, option='Is false', correct=false}]}", exception.getMessage());
    }

    @Test
    void testUpdatedQuestionsPropertiesByIdWithUpdatedDescription() {
        newQuestion.setId(1L);
        newQuestion.setDescription("New Description");

        when(answerService.mapAnswersToAnswerDo(answerList)).thenReturn(answerDoList);
        when(questionRepository.findAllById(any())).thenReturn(List.of(questionWithAnswerList));

        final List<QuestionDo> questionDo = questionService.updatedQuestionsPropertiesById(List.of(newQuestion));
        assertEquals(questionDo.get(0).getId(), questionWithAnswerList.getId());
        assertEquals(questionDo.get(0).getModuleName(), questionWithAnswerList.getModuleName());
        assertEquals(questionDo.get(0).getAnswers().size(), questionWithAnswerList.getAnswers().size());
        assertEquals(questionDo.get(0).getAnswers().get(0).getOption(),
                questionWithAnswerList.getAnswers().get(0).getOption());
        assertEquals(questionDo.get(0).getAnswers().get(0).isCorrect(),
                questionWithAnswerList.getAnswers().get(0).isCorrect());
        assertEquals(questionDo.get(0).getDescription(), newQuestion.getDescription());
    }

    @Test
    void testUpdatedQuestionsPropertiesByIdWithUpdatedModule() {
        newQuestion.setId(1L);
        newQuestion.setModuleName("New Module");

        when(answerService.mapAnswersToAnswerDo(answerList)).thenReturn(answerDoList);
        when(questionRepository.findAllById(any())).thenReturn(List.of(questionWithAnswerList));

        final List<QuestionDo> questionDo = questionService.updatedQuestionsPropertiesById(List.of(newQuestion));
        assertEquals(questionDo.get(0).getId(), questionWithAnswerList.getId());
        assertEquals(questionDo.get(0).getModuleName(), newQuestion.getModuleName());
        assertEquals(questionDo.get(0).getAnswers().size(), questionWithAnswerList.getAnswers().size());
        assertEquals(questionDo.get(0).getAnswers().get(0).getOption(),
                questionWithAnswerList.getAnswers().get(0).getOption());
        assertEquals(questionDo.get(0).getAnswers().get(0).isCorrect(),
                questionWithAnswerList.getAnswers().get(0).isCorrect());
        assertEquals(questionDo.get(0).getDescription(), questionWithAnswerList.getDescription());
    }

    @Test
    void testUpdatedQuestionsPropertiesByIdWithUpdatedAnswers() {
        newQuestion.setId(1L);
        newQuestion.setAnswers(newAnswerList);

        when(answerService.mapAnswersToAnswerDo(newAnswerList)).thenReturn(newAnswerDoList);
        when(questionRepository.findAllById(any())).thenReturn(List.of(questionWithAnswerList));

        final List<QuestionDo> questionDo = questionService.updatedQuestionsPropertiesById(List.of(newQuestion));
        assertEquals(questionDo.get(0).getId(), questionWithAnswerList.getId());
        assertEquals(questionDo.get(0).getModuleName(), questionWithAnswerList.getModuleName());
        assertEquals(questionDo.get(0).getAnswers().size(), newQuestion.getAnswers().size());
        assertEquals(questionDo.get(0).getAnswers().get(0).getOption(), newQuestion.getAnswers().get(0).getOption());
        assertEquals(questionDo.get(0).getAnswers().get(0).isCorrect(), newQuestion.getAnswers().get(0).isCorrect());
        assertEquals(questionDo.get(0).getDescription(), questionWithAnswerList.getDescription());
    }

    public boolean isQuestionEqualWithQuestionDo(final Question question, final QuestionDo questionDo) {
        if (!question.getId().equals(questionDo.getId()) || !question.getDescription()
                .equals(questionDo.getDescription())) {
            return false;
        }

        if ((question.getModuleName() == null) != (questionDo.getModuleName() == null)) {
            return false;
        }

        return (question.getModuleName() == null || question.getModuleName().equals(questionDo.getModuleName()))
                && isAnswerEqualWithAnswerDo(question.getAnswers(), questionDo.getAnswers());
    }

    public boolean isAnswerEqualWithAnswerDo(final List<Answer> answers, final List<AnswerDo> answerDos) {
        if (answers.size() != answerDos.size()) {
            return false;
        }

        return answers.stream().allMatch(answer -> answerDos.stream().anyMatch(
                answerDo -> answer.getOption().equals(answerDo.getOption()) && answer.getId()
                        .equals(answerDo.getId())));
    }

}