package com.simulator.exam.dto;

import java.util.List;
import java.util.Objects;

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

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final QuestionDo that = (QuestionDo) o;
        return id == that.id && Objects.equals(description, that.description) && Objects.equals(answers, that.answers)
                && Objects.equals(moduleName, that.moduleName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, description, answers, moduleName);
    }
}
