package com.simulator.exam.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.simulator.exam.dto.AnswerDo;
import com.simulator.exam.dto.QuestionDo;
import com.simulator.exam.dto.QuestionsStructureDo;
import com.simulator.exam.entity.Answer;
import com.simulator.exam.entity.Question;
import com.simulator.exam.exception.DuplicateQuestionException;
import com.simulator.exam.repository.QuestionRepository;
import com.simulator.exam.util.ExamUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
class QuestionServiceImp implements QuestionService {

    private final QuestionRepository questionRepository;
    private final AnswerService answerService;
    private static final Logger LOGGER = Logger.getLogger("application.logger");
    private static final String MISSING_ENTITY_MESSAGE = "Question with id %s doesn't exist";

    @Setter
    @Value("${app.question.file.locale.path}")
    private String filePath;

    @Autowired
    public QuestionServiceImp(final QuestionRepository questionRepository, final AnswerService answerService) {
        this.questionRepository = questionRepository;
        this.answerService = answerService;
    }

    /**
     * Save imported questions to the database ensuring uniqueness.
     *
     * @param questions the list of questions to save
     */
    private void saveImportedQuestions(final List<Question> questions) {
        if (questions.isEmpty()) {
            return;
        }

        questions.forEach(this::saveUniqueQuestion);
        LOGGER.log(Level.INFO, "{0} unique questions have been successfully saved", questions.size());
    }

    /**
     * Retrieve all questions in random order.
     *
     * @return list of random questions
     */
    public List<QuestionDo> getRandomQuestions() {
        return questionRepository.getQuestionsOrderByRandom().stream().map(this::mapQuestionToQuestionDo).toList();
    }

    /**
     * Retrieve a specified number of random questions for a given module.
     *
     * @param module            the module name
     * @param numberOfQuestions the number of questions to retrieve
     * @return list of random questions
     */
    public List<QuestionDo> getRandomQuestionsByModule(final String module, final int numberOfQuestions) {
        return questionRepository.getTopByModuleEnumQuestionOrderByRandom(numberOfQuestions, module).stream()
                .map(this::mapQuestionToQuestionDo).toList();
    }

    /**
     * Maps a Question entity to a QuestionDo DTO.
     *
     * @param question the question entity
     * @return the mapped QuestionDo
     */
    private QuestionDo mapQuestionToQuestionDo(final Question question) {
        return new QuestionDo(question.getDescription(), answerService.mapAnswersToAnswerDo(question.getAnswers()),
                question.getModuleName());
    }

    /**
     * Save a question ensuring it is unique.
     *
     * @param question the question to save
     */
    private void saveUniqueQuestion(final Question question) {
        if (!questionRepository.findAll().stream().map(Question::getDescription).toList()
                .contains(question.getDescription())) {
            questionRepository.save(new Question(question.getDescription(), List.of(), question.getModuleName()));
        } else {
            LOGGER.log(Level.WARNING, "Question {0} is already persisted", question.getDescription());
            throw new DuplicateQuestionException("Question %s is already persisted.", question.getDescription());
        }
    }

    /**
     * Retrieve questions based on a given structure.
     *
     * @param structureList the list of question structures
     * @return the list of matching questions
     */
    public List<QuestionDo> getQuestionsByStructure(final List<QuestionsStructureDo> structureList) {
        final List<QuestionDo> questions = new ArrayList<>();
        structureList.forEach(structure -> questions.addAll(
                getRandomQuestionsByModule(structure.getModule(), structure.getQuestionNumber())));
        return questions;
    }

    /**
     * Import questions based on the module number from a local file.
     *
     * @param moduleName the name of the module
     */
    public void saveImportLocalQuestionByFileName(final String fileName, final String moduleName) {

        final List<Question> questions =
                ExamUtils.getAllQuestionsFromYamlLocaleFile(String.format(filePath, fileName), moduleName);
        saveImportedQuestions(questions);
        LOGGER.log(Level.INFO,
                String.format("%s questions successfully saved from local file named %s", questions.size(), fileName));
        answerService.saveAnswers(questions, questionRepository.findAll());
    }

    /**
     * Import questions from a file for a specific module.
     *
     * @param multipartFile the uploaded file containing questions
     * @param moduleName    the module name for importing questions
     */
    public void saveImportQuestionsFromFile(final MultipartFile multipartFile, final String moduleName) {
        final List<Question> questions = ExamUtils.getAllQuestionsFromYamlMultipart(multipartFile, moduleName);
        saveImportedQuestions(questions);
        LOGGER.log(Level.INFO,
                String.format("%s questions successfully saved from multipart file named %s", questions.size(),
                        multipartFile));
        answerService.saveAnswers(questions, questionRepository.findAll());
    }

    /**
     * Will return all the stored question from the database
     *
     * @return a list of questions
     */
    @Override
    public List<QuestionDo> getAllQuestions() {
        return questionRepository.findAll().stream().map(this::mapQuestionToQuestionDo).toList();
    }

    /**
     * Will return a question base on the provided ID
     *
     * @param id the question id
     * @return the question
     */
    @Override
    public QuestionDo getQuestionById(final Long id) {
        return mapQuestionToQuestionDo(questionRepository.getReferenceById(id));
    }

    /**
     * Will return all the answers for a specific question id
     *
     * @param id the question id
     * @return the list of answers
     */
    @Override
    public List<AnswerDo> getAnswersForQuestionId(final Long id) {
        return answerService.mapAnswersToAnswerDo(questionRepository.getReferenceById(id).getAnswers());
    }

    /**
     * Will batch save a list of questions
     *
     * @param questions the questions that needs to be saved
     */
    @Override
    public void saveQuestions(final List<Question> questions) {
        questionRepository.saveAll(questions);
    }

    /**
     * Will save a list of answers for a specific question base on question id
     *
     * @param id      the question id
     * @param answers the answers that needs to be saved
     */
    @Override
    public void saveAnswersForThGivenQuestion(final Long id, final List<Answer> answers) {
        final Question question = getQuestionFromDatabaseOrThrowException(id);

        answerService.saveAnswersForTheGivenQuestion(question, answers);
    }

    /**
     * Will batch delete the questions base on the provided list
     *
     * @param question the questions to be deleted
     */
    @Override
    public void deleteGivenQuestion(final List<Question> question) {
        questionRepository.deleteAll(question);
    }

    /**
     * Will delete a question base on the id
     *
     * @param id the question id
     */
    @Override
    public void deleteQuestionById(final long id) {
        questionRepository.deleteById(id);
    }

    /**
     * Method will delete all the answers for the provided question ID
     *
     * @param id the question ID
     */
    @Override
    public void deleteAnswersForGivenQuestion(final long id) {
        final Question question = getQuestionFromDatabaseOrThrowException(id);
        question.setAnswers(List.of());
    }

    /**
     * Will batch update a list of questions base on the provided list
     *
     * @param questions the list of question to be updated
     * @return the updated list of questions
     */
    @Override
    public List<Question> updateQuestion(final List<Question> questions) {
        final Map<Long, Question> savedQuestionsMap =
                questionRepository.findAllById(questions.stream().map(Question::getId).toList()).stream()
                        .collect(Collectors.toMap(Question::getId, q -> q));

        questions.forEach(q -> {
            final Question dbq = savedQuestionsMap.get(q.getId());
            if (dbq != null) {
                dbq.setDescription(q.getDescription());
                dbq.setModuleName(q.getModuleName());
                dbq.setAnswers(q.getAnswers());
            }
        });
        return savedQuestionsMap.values().stream().toList();
    }

    /**
     * Will update a question base on the ID
     *
     * @param question the updated question
     * @param id       the id of the question
     * @return the updated question
     */
    @Override
    public Question updateQuestionById(final Question question, final Long id) {
        final Question dbQuestion = getQuestionFromDatabaseOrThrowException(id);
        dbQuestion.setDescription(question.getDescription());
        dbQuestion.setAnswers(question.getAnswers());
        dbQuestion.setModuleName(question.getModuleName());
        return dbQuestion;
    }

    /**
     * Will update and replace all answers for a specific question base on question id
     *
     * @param answers the new List of answers
     * @param id      the question id
     * @return the updated question
     */
    @Override
    public Question updateQuestionByQuestionId(final List<Answer> answers, final Long id) {
        final Question dbQuestion = getQuestionFromDatabaseOrThrowException(id);
        dbQuestion.setAnswers(answers);
        return dbQuestion;
    }

    @Override
    public Question updatedQuestionPropertiesById(final Long id, final Question question) {
        final Question databaseQuestion = getQuestionFromDatabaseOrThrowException(id);
        return updateOnlyRequiredFields(databaseQuestion, question);
    }

    @Override
    public List<Question> updatedQuestionsPropertiesById(final List<Question> questions) {
        final Map<Long, Question> savedQuestionsMap =
                questionRepository.findAllById(questions.stream().map(Question::getId).toList()).stream()
                        .collect(Collectors.toMap(Question::getId, q -> q));

        questions.forEach(q -> {
            final Question dbq = savedQuestionsMap.get(q.getId());
            if (dbq != null) {
                updateOnlyRequiredFields(dbq, q);
            }
        });
        return savedQuestionsMap.values().stream().toList();
    }

    public Question updateOnlyRequiredFields(final Question databaseQuestion, final Question updateField) {
        if (StringUtils.hasText(updateField.getDescription())) {
            databaseQuestion.setDescription(updateField.getDescription());
        }
        if (StringUtils.hasText(updateField.getModuleName())) {
            databaseQuestion.setModuleName(updateField.getModuleName());
        }
        if (!updateField.getAnswers().isEmpty()) {
            databaseQuestion.setAnswers(updateField.getAnswers());
        }
        return databaseQuestion;
    }

    private Question getQuestionFromDatabaseOrThrowException(final long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format(MISSING_ENTITY_MESSAGE, id)));
    }
}
