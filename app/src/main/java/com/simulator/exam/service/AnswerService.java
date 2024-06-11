package com.simulator.exam.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.simulator.exam.entity.Answer;
import com.simulator.exam.entity.Question;
import com.simulator.exam.repository.AnswerRepository;
import org.springframework.stereotype.Service;

@Service
public class AnswerService {

    private final AnswerRepository answerRepository;

    private static final Logger logger = Logger.getLogger("application.logger");

    public AnswerService(final AnswerRepository answerRepository) {
        this.answerRepository = answerRepository;
    }

    /**
     * Save answers for the given list of questions.
     *
     * @param questionsFromYaml the list of questions with answers to save
     */
    public void saveAnswers(final List<Question> questionsFromYaml, final List<Question> databaseQuestions) {

        final List<Answer> answers = new ArrayList<>();
        for (final Question importedQuestion : questionsFromYaml) {
            final Optional<Question> savedQuestion =
                    databaseQuestions.stream().filter(q -> q.getQuestion().equals(importedQuestion.getQuestion()))
                            .findFirst();

            savedQuestion.ifPresent(question -> importedQuestion.getAnswers().forEach(answer -> {
                answer.setQuestion(question);
                answers.add(answer);
            }));
        }
        answers.forEach(answer -> {
            try {
                answerRepository.save(answer);
            } catch (final RuntimeException e) {
                logger.log(Level.SEVERE, String.format("Duplicate answer: %s", answer.getOption()), e);
                throw e;
            }
        });
    }
}
