package com.simulator.exam.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.simulator.exam.dto.AnswerDo;
import com.simulator.exam.entity.Answer;
import com.simulator.exam.entity.Question;
import com.simulator.exam.exception.DuplicateAnswerException;
import com.simulator.exam.exception.FileQuestionNotPersistedException;
import com.simulator.exam.repository.AnswerRepository;
import jakarta.persistence.EntityExistsException;
import org.springframework.stereotype.Service;

@Service
class AnswerServiceImpl implements AnswerService {

    private static final Logger LOGGER = Logger.getLogger("application.logger");

    private final AnswerRepository answerRepository;

    public AnswerServiceImpl(final AnswerRepository answerRepository) {
        this.answerRepository = answerRepository;
    }

    /**
     * Save answers for the given list of questions.
     *
     * @param questionsFromYaml the list of questions with answers to save
     */
    @Override
    public void saveAnswers(final List<Question> questionsFromYaml, final List<Question> databaseQuestions) {

        final List<Answer> answersToBeSaved = getListOfAnswersNeededToBeSaved(questionsFromYaml, databaseQuestions);

        answersToBeSaved.forEach(answer -> {
            try {
                answerRepository.save(answer);
            } catch (final EntityExistsException e) {
                throw new DuplicateAnswerException("Answer %s for question %s is already persisted", answer.getOption(),
                        answer.getQuestion().getDescription(), e);
            }
        });
        LOGGER.log(Level.INFO, "{0} answers have been saved successfully", answersToBeSaved.size());
    }

    /**
     * Method will compare the saved question with the ones from the file and will get the question id needed for the answer
     * to be linked to the question.
     *
     * @param questionsFromYaml question from the file
     * @param databaseQuestions persisted questions
     * @return a list of Answers containing the question id.
     */
    private List<Answer> getListOfAnswersNeededToBeSaved(final List<Question> questionsFromYaml,
            final List<Question> databaseQuestions) {
        final List<Answer> answers = new ArrayList<>();
        for (final Question importedQuestion : questionsFromYaml) {
            final Optional<Question> savedQuestion =
                    databaseQuestions.stream().filter(q -> q.getDescription().equals(importedQuestion.getDescription()))
                            .findFirst();

            savedQuestion.ifPresentOrElse(question -> importedQuestion.getAnswers().forEach(answer -> {
                answer.setQuestion(question);
                answers.add(answer);
            }), () -> {
                throw new FileQuestionNotPersistedException("Question {0} from file is not sored in the database",
                        importedQuestion.getDescription());
            });
        }
        return answers;
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
}
