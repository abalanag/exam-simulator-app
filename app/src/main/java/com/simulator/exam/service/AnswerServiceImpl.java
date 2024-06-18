package com.simulator.exam.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.simulator.exam.dto.AnswerDo;
import com.simulator.exam.entity.Answer;
import com.simulator.exam.repository.AnswerRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

@Service
class AnswerServiceImpl implements AnswerService {
    private static final String MISSING_ENTITY_MESSAGE = "Answer with id %s doesn't exist";

    private final AnswerRepository answerRepository;

    private static final String MISSING_ANSWER_ID = "Missing id for provided answer: %s";

    public AnswerServiceImpl(final AnswerRepository answerRepository) {
        this.answerRepository = answerRepository;
    }

    /**
     * Maps a list of Answer entities to a list of AnswerDo DTOs.
     *
     * @param answers the list of answer entities
     * @return the list of mapped AnswerDo
     */
    @Override
    public List<AnswerDo> mapAnswersToAnswerDo(final List<Answer> answers) {
        return answers.stream().map(a -> new AnswerDo(a.getId(), a.getOption(), a.isCorrect())).toList();
    }

    /**
     * Will batch update a list of answers
     *
     * @param answers a list of answers
     * @return will return the updated list
     */
    @Override
    public List<Answer> updateAnswers(final List<Answer> answers) {
        final Map<Long, Answer> savedAnswersMap =
                answerRepository.findAllById(answers.stream().map(Answer::getId).toList()).stream()
                        .collect(Collectors.toMap(Answer::getId, a -> a));

        answers.forEach(a -> {
            final Answer dba = savedAnswersMap.get(a.getId());
            if (a.getId() == null || a.getId() <= 0) {
                throw new EntityNotFoundException(String.format(MISSING_ANSWER_ID, a));
            }
            if (dba != null) {
                dba.setCorrect(a.isCorrect());
                dba.setOption(a.getOption());
            }
        });
        return savedAnswersMap.values().stream().toList();
    }

    /**
     * Will update an answers base of the provided id
     *
     * @param answer the updated answer
     * @param id     the id of the answer
     * @return the updated answer
     */
    @Override
    public Answer updateAnswerById(final Answer answer, final Long id) {
        final Answer dbAnswer = answerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format(MISSING_ENTITY_MESSAGE, id)));
        dbAnswer.setOption(answer.getOption());
        dbAnswer.setCorrect(answer.isCorrect());
        return dbAnswer;
    }
}
