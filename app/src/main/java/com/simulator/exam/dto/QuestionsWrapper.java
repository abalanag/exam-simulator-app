package com.simulator.exam.dto;

import java.util.List;

import com.simulator.exam.entity.Question;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionsWrapper {

    private List<Question> questions;

}
