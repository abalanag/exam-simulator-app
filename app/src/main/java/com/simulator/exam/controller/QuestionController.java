package com.simulator.exam.controller;

import java.util.List;

import com.simulator.exam.dto.QuestionDo;
import com.simulator.exam.dto.QuestionsStructureDo;
import com.simulator.exam.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(path = "/v1/questions")
@Transactional
public class QuestionController {

    private final QuestionService questionService;

    @Autowired
    public QuestionController(final QuestionService questionService) {
        this.questionService = questionService;
    }

    /**
     * Imports questions based on the module number from a file with the given name.
     *
     * @param moduleNumber The module number for importing questions.
     * @return HTTP status indicating success (ACCEPTED).
     */
    @PostMapping(value = { "/import/{fileName}/{moduleNumber}", "/import/{fileName}" })
    public HttpStatus importLocalQuestionsToDatabase(@PathVariable("fileName") final String fileName,
            @PathVariable(value = "moduleNumber", required = false) final Integer moduleNumber) {
        try {
            questionService.saveImportLocalQuestionByModuleNumber(fileName, moduleNumber);
            return HttpStatus.ACCEPTED;
        } catch (final Exception e) {
            // Handle the exception, e.g., log the error
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    /**
     * Retrieves all questions randomly.
     *
     * @return List of randomly selected questions.
     */
    @GetMapping
    public ResponseEntity<List<QuestionDo>> getAllQuestionsRandom() {
        try {
            final List<QuestionDo> questions = questionService.getRandomQuestions();
            return ResponseEntity.ok(questions);
        } catch (final Exception e) {
            // Handle the exception, e.g., log the error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Retrieves a specified number of random questions for a given module.
     *
     * @param module            The module name.
     * @param numberOfQuestions The desired number of questions.
     * @return List of randomly selected questions.
     */
    @GetMapping("/random")
    public ResponseEntity<List<QuestionDo>> getRandomQuestionsByModuleAndNumber(@RequestParam final String module,
            @RequestParam final int numberOfQuestions) {
        try {
            final List<QuestionDo> questions = questionService.getRandomQuestionsByModule(module, numberOfQuestions);
            return ResponseEntity.ok(questions);
        } catch (final Exception e) {
            // Handle the exception, e.g., log the error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Retrieves questions based on a given structure.
     *
     * @param questionDo List of question structures.
     * @return List of matching questions.
     */
    @GetMapping("/exam-structure")
    public ResponseEntity<List<QuestionDo>> getQuestionStructure(
            @RequestBody final List<QuestionsStructureDo> questionDo) {
        try {
            final List<QuestionDo> questions = questionService.getQuestionsByStructure(questionDo);
            return ResponseEntity.ok(questions);
        } catch (final Exception e) {
            // Handle the exception, e.g., log the error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Imports questions from a file for a specific module.
     *
     * @param multipartFile The uploaded file containing questions.
     * @param moduleNumber  The module number for importing questions.
     * @return HTTP status indicating success or failure.
     */
    @PostMapping(value = "/import/from-file/{moduleNumber}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public HttpStatus importQuestionsToDatabaseFromFile(@RequestPart("file") final MultipartFile multipartFile,
            @PathVariable final Integer moduleNumber) {
        try {
            questionService.saveImportQuestionsFromFile(multipartFile, moduleNumber);
            return HttpStatus.ACCEPTED;
        } catch (final Exception e) {
            // Handle the exception, e.g., log the error
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }
}
