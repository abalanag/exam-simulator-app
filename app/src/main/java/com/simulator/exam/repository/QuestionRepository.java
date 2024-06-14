package com.simulator.exam.repository;

import java.util.List;

import com.simulator.exam.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    @Query(value = "select * from questions q where q.module_name = :qModule order by random() limit :limitNumber",
            nativeQuery = true)
    List<Question> getTopByModuleEnumQuestionOrderByRandom(@Param("limitNumber") int limit,
            @Param("qModule") String module);

    @Query(value = "select * from questions order by random()", nativeQuery = true)
    List<Question> getQuestionsOrderByRandom();

    @Query("SELECT COUNT(q) > 0 FROM Question q WHERE q.description = :qDescription")
    boolean isQuestionAlreadySaved(@Param("qDescription") String description);

    @Query("SELECT COUNT(q) = 0 FROM Question q WHERE q.moduleName = :qModule")
    boolean moduleHasNoQuestions(@Param("qModule") String module);

    List<Question> findAllByDescriptionIn(List<String> descriptions);
}
