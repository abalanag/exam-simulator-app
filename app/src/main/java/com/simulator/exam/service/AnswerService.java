package com.simulator.exam.service;

import java.util.List;

import com.simulator.exam.dto.AnswerDo;
import com.simulator.exam.entity.Answer;
import com.simulator.exam.entity.Question;

public interface AnswerService {

    void saveAnswers(final List<Question> questionsFromYaml, final List<Question> databaseQuestions);

    List<AnswerDo> mapAnswersToAnswerDo(final List<Answer> answers);
    void saveAnswersForTheGivenQuestion(Question question, List<Answer> answers);

    List<Answer> updateAnswers(List<Answer> answer);

    Answer updateAnswerById(Answer answer, Long id);
}
