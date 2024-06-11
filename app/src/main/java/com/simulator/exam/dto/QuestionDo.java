package com.simulator.exam.dto;

import java.util.List;

import com.simulator.exam.entity.ModuleEnum;
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
    private String question;
    private List<AnswerDo> answers;

    @Nullable
    private ModuleEnum moduleEnum;

    public QuestionDo(final String question, final List<AnswerDo> answers, @Nullable final ModuleEnum moduleEnum) {
        this.question = question;
        this.answers = answers;
        this.moduleEnum = moduleEnum;
    }
}
