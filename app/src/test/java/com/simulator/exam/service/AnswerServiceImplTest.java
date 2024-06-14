package com.simulator.exam.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.List;

import com.simulator.exam.dto.AnswerDo;
import com.simulator.exam.entity.Answer;
import com.simulator.exam.repository.AnswerRepository;
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

    @Test
    void testMapAnswersToAnswerDo() {
        final List<Answer> answers = List.of(new Answer("Programming language", true));

        final List<AnswerDo> answerDos = answerServiceImpl.mapAnswersToAnswerDo(answers);

        assertEquals(1, answerDos.size());
        assertEquals("Programming language", answerDos.get(0).getOption());
        assertTrue(answerDos.get(0).isCorrect());
    }

    @Test
    void testSaveAnswersWithEmptyLists() {
        verify(answerRepository, never()).save(any(Answer.class));
    }
}