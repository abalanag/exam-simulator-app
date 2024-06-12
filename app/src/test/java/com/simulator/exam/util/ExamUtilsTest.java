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
import com.simulator.exam.exception.LocalFileNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.yaml.snakeyaml.constructor.ConstructorException;

class ExamUtilsTest {

    private static final ModuleEnum MODULE_ENUM = ModuleEnum.SPRING_AOP;
    private ModuleEnum invalidModule;
    private static final String TEST_FILE_NAME = "test-questions.yaml";

    private final String INVALID_FILE_NAME = "missing-test.yml";

    private final String WRONG_FORMAT_FILE_NAME = "test-wrong-format-questions.yaml";

    private final String validYamlData = """
            questions:
              - description: "What is the capital of Canada?"
                answers:
                  - option: "Toronto"
                    correct: true
                  - option: "Ottawa"
                    correct: false
                  - option: "Vancouver"
                    correct: false
              - description: "What is 2 + 2?"
                answers:
                  - option: "3"
                    correct: false
                  - option: "4"
                    correct: true
                  - option: "5"
                    correct: false""";

    @BeforeEach
    void setUp() {
        ExamUtils.setFilePath("src/test/resources/questions-files/%s");
    }

    @Test
    void testGetAllQuestionsFromYamlLocaleFilePositive() {
        final List<Question> questions = ExamUtils.getAllQuestionsFromYamlLocaleFile(TEST_FILE_NAME, MODULE_ENUM);
        assertNotNull(questions);
        assertEquals(2, questions.size());
        assertEquals("What is the capital of Canada?", questions.get(0).getDescription());
    }

    @Test
    void testGetAllQuestionsFromYamlLocaleFileWithNullFileName() {
        assertThrows(IllegalArgumentException.class, () -> ExamUtils.getAllQuestionsFromYamlLocaleFile(null, null));
    }

    @Test
    void testGetAllQuestionsFromYamlLocaleFileWithEmptyFileName() {
        assertThrows(IllegalArgumentException.class, () -> ExamUtils.getAllQuestionsFromYamlLocaleFile("", null));
    }

    @Test
    void testGetAllQuestionsFromYamlLocaleFileNegative() {
        assertThrows(LocalFileNotFoundException.class,
                () -> ExamUtils.getAllQuestionsFromYamlLocaleFile(INVALID_FILE_NAME, invalidModule));
    }

    @Test
    void testGetAllQuestionsFromYamlLocaleFileWrongFormat() {
        assertThrows(ConstructorException.class,
                () -> ExamUtils.getAllQuestionsFromYamlLocaleFile(WRONG_FORMAT_FILE_NAME, invalidModule));
    }

    @Test
    void testGetAllQuestionsFromYamlMultipartPositive() throws IOException {
        final MultipartFile mockMultipartFile = mock(MultipartFile.class);
        when(mockMultipartFile.getInputStream()).thenReturn(
                new MockMultipartFile("file", validYamlData.getBytes()).getInputStream());

        final List<Question> questions = ExamUtils.getAllQuestionsFromYamlMultipart(mockMultipartFile, MODULE_ENUM);
        assertNotNull(questions);
        assertEquals(2, questions.size());
        assertEquals("What is 2 + 2?", questions.get(1).getDescription());
    }

    @Test
    void testGetAllQuestionsFromYamlMultipartNegative() throws IOException {
        final MultipartFile emptyMultipartFile = mock(MultipartFile.class);
        when(emptyMultipartFile.getInputStream()).thenReturn(
                new MockMultipartFile("empty", new byte[0]).getInputStream());

        assertThrows(RuntimeException.class,
                () -> ExamUtils.getAllQuestionsFromYamlMultipart(emptyMultipartFile, MODULE_ENUM));
    }
}
