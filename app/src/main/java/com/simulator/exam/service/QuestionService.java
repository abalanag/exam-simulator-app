package com.simulator.exam.service;

import java.util.List;

import com.simulator.exam.dto.QuestionDo;
import com.simulator.exam.dto.QuestionsStructureDo;
import org.springframework.web.multipart.MultipartFile;

public interface QuestionService {

    List<QuestionDo> getRandomQuestions();
    List<QuestionDo> getRandomQuestionsByModule(final String module, final int numberOfQuestions);
    List<QuestionDo> getQuestionsByStructure(final List<QuestionsStructureDo> structureList);
    void saveImportLocalQuestionByModuleNumber(final String fileName, final Integer moduleNumber);
    void saveImportQuestionsFromFile(final MultipartFile multipartFile, final Integer moduleNumber);
}
