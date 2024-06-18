package com.simulator.exam.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.simulator.exam.dto.AnswerDo;
import com.simulator.exam.entity.Answer;
import com.simulator.exam.repository.AnswerRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AnswerServiceImplTest {

    @Mock
    private AnswerRepository answerRepository;

    @InjectMocks
    private AnswerServiceImpl answerServiceImpl;

    private List<Answer> updatedAnswers;

    private List<Answer> existingAnswers;

    @BeforeEach
    public void setUp() {
        updatedAnswers = new ArrayList<>();
        updatedAnswers.add(new Answer(1L, "Updated Option A", false));
        updatedAnswers.add(new Answer(2L, "Updated Option B", true));

        existingAnswers = new ArrayList<>();
        existingAnswers.add(new Answer(1L, "Option A", true));
        existingAnswers.add(new Answer(2L, "Option B", false));
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
    void testMapAnswersToAnswerDoWithEmptyList() {
        final List<Answer> answers = List.of();

        final List<AnswerDo> answerDos = answerServiceImpl.mapAnswersToAnswerDo(answers);
        assertEquals(0, answerDos.size());
    }

    @Test
    void testUpdateAnswers() {
        when(answerRepository.findAllById(anyList())).thenReturn(existingAnswers);

        final List<Answer> result = answerServiceImpl.updateAnswers(updatedAnswers);

        assertEquals(updatedAnswers.get(0).getOption(), result.get(0).getOption());
        assertEquals(updatedAnswers.get(0).isCorrect(), result.get(0).isCorrect());
        assertEquals(updatedAnswers.get(1).getOption(), result.get(1).getOption());
        assertEquals(updatedAnswers.get(1).isCorrect(), result.get(1).isCorrect());
    }

    @Test
    void testUpdateAnswersWithMissingId() {
        updatedAnswers.get(0).setId(null);

        when(answerRepository.findAllById(anyList())).thenReturn(existingAnswers);

        assertThrows(EntityNotFoundException.class, () -> answerServiceImpl.updateAnswers(updatedAnswers));
    }

    @Test
    void testUpdateAnswersWithNoDatabaseQuestions() {
        when(answerRepository.findAllById(anyList())).thenReturn(List.of());

        assertEquals(0, answerServiceImpl.updateAnswers(updatedAnswers).size());
    }

    @Test
    void testUpdateAnswerByIdSuccess() {
        final Long answerId = 1L;

        when(answerRepository.findById(answerId)).thenReturn(Optional.of(existingAnswers.get(0)));

        final Answer result = answerServiceImpl.updateAnswerById(updatedAnswers.get(0), answerId);

        assertEquals(updatedAnswers.get(0), result);
    }

    @Test
    void testUpdateAnswerByIdNotFound() {
        final Long answerId = 1L;
        final Answer expectedAnswer = updatedAnswers.get(0);

        when(answerRepository.findById(answerId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> answerServiceImpl.updateAnswerById(expectedAnswer, answerId));
    }

}