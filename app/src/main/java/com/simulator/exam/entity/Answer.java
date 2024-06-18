package com.simulator.exam.entity;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "answers")
@Getter
@Setter
@NoArgsConstructor
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "answer_sequence")
    @SequenceGenerator(initialValue = 1000, name = "answer_sequence", sequenceName = "answer_sequence",
            allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;
    private String option;
    private boolean correct;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    public Answer(final String option, final boolean correct) {
        this.option = option;
        this.correct = correct;
    }

    public Answer(final Long id, final String option, final boolean correct) {
        this.id = id;
        this.option = option;
        this.correct = correct;
    }

    @Override
    public String toString() {
        return "Answer{" + "id=" + id + ", option='" + option + '\'' + ", correct=" + correct + '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Answer answer = (Answer) o;
        return correct == answer.correct && Objects.equals(id, answer.id) && Objects.equals(option, answer.option)
                && Objects.equals(question, answer.question);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, option, correct, question);
    }
}
