package com.simulator.exam.dto;

import java.util.List;

import com.simulator.exam.entity.Question;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class QuestionsWrapper {

    private List<Question> questions;
}
