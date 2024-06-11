package com.simulator.exam.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;

import com.simulator.exam.entity.ModuleEnum;
import com.simulator.exam.entity.Question;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

class ExamUtilsTest {

    private ModuleEnum validModule;
    private ModuleEnum invalidModule;
    private String testFileName;

    private final String INVALID_FILE_NAME = "test.yml";

    private final String validYamlData = """
            questions:
              - question: "What is the capital of Canada?"
                answers:
                  - answer: "Toronto"
                    correct: true
                  - answer: "Ottawa"
                    correct: false
                  - answer: "Vancouver"
                    correct: false
              - question: "What is 2 + 2?"
                answers:
                  - answer: "3"
                    correct: false
                  - answer: "4"
                    correct: true
                  - answer: "5"
                    correct: false""";

    @BeforeEach
    void setUp() {
        validModule = ModuleEnum.SPRING_AOP;
        testFileName = "questions-files/Test_module.yaml";
    }

    @Test
    void testGetAllQuestionsFromYamlLocaleFilePositive() {
        final List<Question> questions = ExamUtils.getAllQuestionsFromYamlLocaleFile(testFileName, validModule);
        assertNotNull(questions);
        assertEquals(2, questions.size());
        assertEquals("What is the capital of Canada?", questions.get(0).getQuestion());
    }

    @Test
    void testGetAllQuestionsFromYamlLocaleFileNegative() {
        assertThrows(RuntimeException.class, () -> ExamUtils.getAllQuestionsFromYamlLocaleFile(INVALID_FILE_NAME, invalidModule));
    }

    @Test
    void testGetAllQuestionsFromYamlMultipartPositive() throws IOException {
        final MultipartFile mockMultipartFile = mock(MultipartFile.class);
        when(mockMultipartFile.getInputStream()).thenReturn(
                new MockMultipartFile("file", validYamlData.getBytes()).getInputStream());

        final List<Question> questions = ExamUtils.getAllQuestionsFromYamlMultipart(mockMultipartFile, validModule);
        assertNotNull(questions);
        assertEquals(2, questions.size());
        assertEquals("What is 2 + 2?", questions.get(1).getQuestion());
    }

    @Test
    void testGetAllQuestionsFromYamlMultipartNegative() throws IOException {
        final MultipartFile emptyMultipartFile = mock(MultipartFile.class);
        when(emptyMultipartFile.getInputStream()).thenReturn(
                new MockMultipartFile("empty", new byte[0]).getInputStream());

        assertThrows(RuntimeException.class,
                () -> ExamUtils.getAllQuestionsFromYamlMultipart(emptyMultipartFile, validModule));
    }
}
