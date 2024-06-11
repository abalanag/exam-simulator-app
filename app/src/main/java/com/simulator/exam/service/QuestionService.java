package com.simulator.exam.service;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.simulator.exam.dto.AnswerDo;
import com.simulator.exam.dto.QuestionDo;
import com.simulator.exam.dto.QuestionsStructureDo;
import com.simulator.exam.entity.Answer;
import com.simulator.exam.entity.ModuleEnum;
import com.simulator.exam.entity.Question;
import com.simulator.exam.exception.DuplicateQuestionException;
import com.simulator.exam.repository.QuestionRepository;
import com.simulator.exam.util.ExamUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class QuestionService {

    private static final Logger logger = Logger.getLogger("application.logger");

    private final QuestionRepository questionRepository;

    private final AnswerService answerService;

    @Autowired
    public QuestionService(final QuestionRepository questionRepository, final AnswerService answerService) {
        this.questionRepository = questionRepository;
        this.answerService = answerService;
    }

    /**
     * Save imported questions to the database ensuring uniqueness.
     *
     * @param questions the list of questions to save
     * @return true if the questions were saved, false otherwise
     */
    private boolean saveImportedQuestions(final List<Question> questions) {
        if (questions.isEmpty()) {
            return false;
        }

        questions.forEach(q -> {
            try {
                saveUniqueQuestion(q);
            } catch (final DuplicateQuestionException e) {
                logger.log(Level.SEVERE, String.format("Duplicate question: %s", q.getQuestion()), e);
                throw e;
            }
        });
        return true;
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
        return new QuestionDo(question.getQuestion(), mapAnswersToAnswerDo(question.getAnswers()),
                question.getModuleEnum());
    }

    /**
     * Maps a list of Answer entities to a list of AnswerDo DTOs.
     *
     * @param answers the list of answer entities
     * @return the list of mapped AnswerDo
     */
    private List<AnswerDo> mapAnswersToAnswerDo(final List<Answer> answers) {
        return answers.stream().map(a -> new AnswerDo(a.getId(), a.getOption(), a.isCorrect())).toList();
    }

    /**
     * Save a question ensuring it is unique.
     *
     * @param question the question to save
     */
    private void saveUniqueQuestion(final Question question) {
        if (!questionRepository.findAll().stream().map(Question::getQuestion).toList()
                .contains(question.getQuestion())) {
            questionRepository.save(new Question(question.getQuestion(), List.of(), question.getModuleEnum()));
        } else {
            throw new DuplicateQuestionException(question.getQuestion() + " already exists");
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
     * @param moduleNumber the module number for importing questions
     */
    public void saveImportLocalQuestionByModuleNumber(final String fileName, final Integer moduleNumber) {
        final ModuleEnum module = ModuleEnum.byModuleNumber(moduleNumber);
        final List<Question> questions = ExamUtils.getAllQuestionsFromYamlLocaleFile(fileName, module);
        saveImportedQuestions(questions);
        answerService.saveAnswers(questions, questionRepository.findAll());
    }

    /**
     * Import questions from a file for a specific module.
     *
     * @param multipartFile the uploaded file containing questions
     * @param moduleNumber  the module number for importing questions
     */
    public void saveImportQuestionsFromFile(final MultipartFile multipartFile, final Integer moduleNumber) {
        final List<Question> questions =
                ExamUtils.getAllQuestionsFromYamlMultipart(multipartFile, ModuleEnum.byModuleNumber(moduleNumber));
        saveImportedQuestions(questions);
        answerService.saveAnswers(questions, questionRepository.findAll());
    }
}
