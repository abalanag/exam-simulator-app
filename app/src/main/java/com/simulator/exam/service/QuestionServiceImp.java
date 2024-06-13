package com.simulator.exam.service;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.simulator.exam.dto.QuestionDo;
import com.simulator.exam.dto.QuestionsStructureDo;
import com.simulator.exam.entity.Question;
import com.simulator.exam.exception.DuplicateQuestionException;
import com.simulator.exam.repository.QuestionRepository;
import com.simulator.exam.util.ExamUtils;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
class QuestionServiceImp implements QuestionService {

    private final QuestionRepository questionRepository;
    private final AnswerService answerService;
    private static final Logger LOGGER = Logger.getLogger("application.logger");

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
            questionRepository.save(
                    new Question(question.getDescription(), List.of(), question.getModuleName()));
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
}
