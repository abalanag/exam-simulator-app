package com.simulator.exam.entity;

import java.util.List;

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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "questions", uniqueConstraints = @UniqueConstraint(columnNames = "question"))
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "question_sequence")
    @SequenceGenerator(initialValue = 1000, name = "question_sequence", sequenceName = "question_sequence",
            allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;
    private String description;

    private String moduleName;

    @OneToMany(mappedBy = "question", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Answer> answers;

    public Question(final String description, final List<Answer> answers, final String moduleName) {
        this.description = description;
        this.answers = answers;
        this.moduleName = moduleName;
    }
}
