package com.simulator.exam.controller;

import java.util.List;

import com.simulator.exam.entity.Answer;
import com.simulator.exam.service.AnswerService;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/v1/answers")
@Transactional
public class AnswerController {

    private final AnswerService answerService;

    public AnswerController(final AnswerService answerService) {
        this.answerService = answerService;
    }

    @PutMapping
    public ResponseEntity<List<Answer>> updateAnswers(@RequestBody final List<Answer> answer) {
        return ResponseEntity.ok(answerService.updateAnswers(answer));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Answer> updateAnswer(@RequestBody final Answer answer, @PathVariable final Long id) {
        return ResponseEntity.ok(answerService.updateAnswerById(answer, id));
    }
}
