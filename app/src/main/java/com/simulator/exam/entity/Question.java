package com.simulator.exam.entity;

import java.util.List;
import java.util.Objects;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "questions", uniqueConstraints = @UniqueConstraint(columnNames = "question"))
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "question_sequence")
    @SequenceGenerator(initialValue = 1000, name = "question_sequence", sequenceName = "question_sequence",
            allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;
    private String description;

    @Nullable
    private String moduleName;

    @OneToMany(mappedBy = "question", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Answer> answers;

    public Question(final String description, final List<Answer> answers, @Nullable final String moduleName) {
        this.description = description;
        this.answers = answers;
        this.moduleName = moduleName;
    }

    public void setAnswers(final List<Answer> answers) {
        this.answers = answers;
        if (answers != null) {
            answers.forEach(answer -> answer.setQuestion(this));
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Question question = (Question) o;
        return Objects.equals(id, question.id) && Objects.equals(description, question.description) && Objects.equals(
                moduleName, question.moduleName) && Objects.equals(answers, question.answers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, description, moduleName, answers);
    }

    @Override
    public String toString() {
        return "Question{" + "id=" + id + ", description='" + description + '\'' + ", moduleName='" + moduleName + '\''
                + ", answers=" + answers + '}';
    }
}
