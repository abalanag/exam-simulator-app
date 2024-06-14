package com.simulator.exam.service;

import java.util.List;

import com.simulator.exam.dto.AnswerDo;
import com.simulator.exam.entity.Answer;

public interface AnswerService {

    List<AnswerDo> mapAnswersToAnswerDo(final List<Answer> answers);

    List<Answer> updateAnswers(List<Answer> answer);

    Answer updateAnswerById(Answer answer, Long id);
}
