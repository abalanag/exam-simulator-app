package com.simulator.exam.dto;

import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
@NoArgsConstructor
public class AnswerDo {

    private Long id;
    private String option;
    private boolean correct;

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final AnswerDo answerDo = (AnswerDo) o;
        return correct == answerDo.correct && Objects.equals(id, answerDo.id) && Objects.equals(option,
                answerDo.option);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, option, correct);
    }

    @Override
    public String toString() {
        return "AnswerDo{" + "id=" + id + ", option='" + option + '\'' + ", correct=" + correct + '}';
    }
}
