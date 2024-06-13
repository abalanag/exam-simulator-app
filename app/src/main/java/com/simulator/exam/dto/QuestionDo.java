package com.simulator.exam.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDo {

    private long id;
    private String description;
    private List<AnswerDo> answers;

    @Nullable
    private String moduleName;

    public QuestionDo(final String description, final List<AnswerDo> answers, @Nullable final String moduleName) {
        this.description = description;
        this.answers = answers;
        this.moduleName = moduleName;
    }
}
