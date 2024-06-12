package com.simulator.exam.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Collections;
import java.util.List;

import com.simulator.exam.dto.AnswerDo;
import com.simulator.exam.entity.Answer;
import com.simulator.exam.entity.ModuleEnum;
import com.simulator.exam.entity.Question;
import com.simulator.exam.exception.DuplicateAnswerException;
import com.simulator.exam.exception.FileQuestionNotPersistedException;
import com.simulator.exam.repository.AnswerRepository;
import jakarta.persistence.EntityExistsException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AnswerServiceImplTest {

    @Mock
    private AnswerRepository answerRepository;

    @InjectMocks
    private AnswerServiceImpl answerServiceImpl;

    @Captor
    private ArgumentCaptor<Answer> answerCaptor;

    @Test
    void testSaveAnswersSuccessfully() {
        final List<Question> questionsFromYaml =
                List.of(new Question("What is Java?", List.of(new Answer("Programming language", true)),
                        ModuleEnum.SPRING_AOP));

        final List<Question> databaseQuestions =
                List.of(new Question("What is Java?", List.of(), ModuleEnum.SPRING_AOP));

        answerServiceImpl.saveAnswers(questionsFromYaml, databaseQuestions);

        verify(answerRepository, times(1)).save(answerCaptor.capture());
        final Answer capturedAnswer = answerCaptor.getValue();
        assertEquals("Programming language", capturedAnswer.getOption());
        assertEquals("What is Java?", capturedAnswer.getQuestion().getDescription());
    }

    @Test
    void testSaveAnswersWithDuplicateAnswerException() {
        final List<Question> questionsFromYaml =
                List.of(new Question("What is Java?", List.of(new Answer("Programming language", true)),
                        ModuleEnum.SPRING_AOP));

        final List<Question> databaseQuestions =
                List.of(new Question("What is Java?", List.of(), ModuleEnum.SPRING_AOP));

        doThrow(EntityExistsException.class).when(answerRepository).save(any(Answer.class));

        final DuplicateAnswerException exception = assertThrows(DuplicateAnswerException.class,
                () -> answerServiceImpl.saveAnswers(questionsFromYaml, databaseQuestions));

        assertTrue(exception.getMessage()
                .contains("Answer Programming language for question What is Java? is already persisted"));
    }

    @Test
    void testMapAnswersToAnswerDo() {
        final List<Answer> answers = List.of(new Answer("Programming language", true));

        final List<AnswerDo> answerDos = answerServiceImpl.mapAnswersToAnswerDo(answers);

        assertEquals(1, answerDos.size());
        assertEquals("Programming language", answerDos.get(0).getOption());
        assertTrue(answerDos.get(0).isCorrect());
    }

    @Test
    void testSaveAnswersWithNullInputs() {
        assertThrows(NullPointerException.class, this::callSaveAnswersWithBothNull);
        assertThrows(NullPointerException.class, this::callSaveAnswersWithFirstNull);
        assertThrows(NullPointerException.class, this::callSaveAnswersWithSecondNull);
    }

    @Test
    void testSaveAnswersWithEmptyLists() {
        answerServiceImpl.saveAnswers(Collections.emptyList(), Collections.emptyList());
        verify(answerRepository, never()).save(any(Answer.class));
    }

    @Test
    void testSaveAnswersWithMismatchedQuestions() {
        final List<Question> questionsFromYaml =
                List.of(new Question("What is Java?", List.of(new Answer("Programming language", true)),
                        ModuleEnum.SPRING_AOP));

        final List<Question> databaseQuestions =
                List.of(new Question("What is Python?", List.of(), ModuleEnum.SPRING_AOP));

        assertThrows(FileQuestionNotPersistedException.class,
                () -> answerServiceImpl.saveAnswers(questionsFromYaml, databaseQuestions));

        verify(answerRepository, never()).save(any(Answer.class));
    }

    @Test
    void testSaveAnswersWithNoMatchingDatabaseQuestions() {
        final List<Question> questionsFromYaml =
                List.of(new Question("What is Java?", List.of(new Answer("Programming language", true)),
                        ModuleEnum.SPRING_AOP));

        final List<Question> databaseQuestions = Collections.emptyList();

        assertThrows(FileQuestionNotPersistedException.class,
                () -> answerServiceImpl.saveAnswers(questionsFromYaml, databaseQuestions));

        verify(answerRepository, never()).save(any(Answer.class));
    }

    private void callSaveAnswersWithBothNull() {
        answerServiceImpl.saveAnswers(null, null);
    }

    private void callSaveAnswersWithFirstNull() {
        answerServiceImpl.saveAnswers(null, Collections.emptyList());
    }

    private void callSaveAnswersWithSecondNull() {
        answerServiceImpl.saveAnswers(
                List.of(new Question("How are you", List.of(new Answer("Good", true)), ModuleEnum.SPRING_AOP)), null);
    }
}