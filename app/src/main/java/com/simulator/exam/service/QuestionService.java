package com.simulator.exam.service;

import java.util.List;

import com.simulator.exam.dto.AnswerDo;
import com.simulator.exam.dto.QuestionDo;
import com.simulator.exam.dto.QuestionsStructureDo;
import com.simulator.exam.entity.Answer;
import com.simulator.exam.entity.Question;
import org.springframework.web.multipart.MultipartFile;

public interface QuestionService {

    List<QuestionDo> getRandomQuestions();
    List<QuestionDo> getRandomQuestionsByModule(final String module, final int numberOfQuestions);
    List<QuestionDo> getQuestionsByStructure(final List<QuestionsStructureDo> structureList);
    void saveImportLocalQuestionByFileName(final String fileName, final String moduleName);
    void saveImportQuestionsFromFile(final MultipartFile multipartFile, final String moduleName);
    List<QuestionDo> getAllQuestions();
    QuestionDo getQuestionById(Long id);

    List<AnswerDo> getAnswersForQuestionId(Long id);

    void saveQuestions(List<Question> questions);
    void saveAnswersForThGivenQuestion(Long id, List<Answer> question);

    void deleteGivenQuestion(List<Question> question);

    void deleteQuestionById(long id);

    void deleteAnswersForGivenQuestion(long id);

    List<QuestionDo> updateQuestion(List<Question> questions);

    QuestionDo updateQuestionById(Question question, Long id);

    QuestionDo updateQuestionByQuestionId(List<Answer> answers, Long id);

    QuestionDo updatedQuestionPropertiesById(Long id, Question question);

    List<QuestionDo> updatedQuestionsPropertiesById(List<Question> question);
}
