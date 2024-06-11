package com.simulator.exam.repository;

import java.util.List;

import com.simulator.exam.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    @Query(value = "select * from questions order by random() limit :limitNumber", nativeQuery = true)
    List<Question> getTopOrderByRandom(@Param("limitNumber") int limit);

    @Query(value = "select * from questions q where q.module_enum = :module order by random() limit :limitNumber",
            nativeQuery = true)
    List<Question> getTopByModuleEnumQuestionOrderByRandom(@Param("limitNumber") int limit,
            @Param("module") String moduleEnum);

    @Query(value = "select * from questions order by random()", nativeQuery = true)
    List<Question> getQuestionsOrderByRandom();
}
