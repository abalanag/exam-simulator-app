package com.simulator.exam.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;

import com.simulator.exam.entity.Question;
import com.simulator.exam.exception.LocalFileNotFoundException;
import com.simulator.exam.exception.MultipartFileLoaderException;
import com.simulator.exam.exception.QuestionLoaderException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.yaml.snakeyaml.constructor.ConstructorException;

@ExtendWith(MockitoExtension.class)
class ExamUtilsTest {

    private static final String MODULE_NAME = "SPRING_AOP";

    @Mock
    private MultipartFile mockMultipartFile;

    private static final String TEST_FILE_NAME = "src/test/resources/questions-files/test-questions.yaml";

    private final String INVALID_FILE_NAME = "src/test/resources/questions-files/missing-test.yml";

    private final String WRONG_FORMAT_FILE_NAME = "src/test/resources/questions-files/test-wrong-format-questions.yaml";

    private final String EMPTY_QUESTIONS_FILE_NAME = "src/test/resources/questions-files/test-questions-empty.yaml";

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

    @Test
    void testGetAllQuestionsFromYamlLocaleFilePositive() {
        final List<Question> questions = ExamUtils.getAllQuestionsFromYamlLocaleFile(TEST_FILE_NAME, MODULE_NAME);
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
                () -> ExamUtils.getAllQuestionsFromYamlLocaleFile(INVALID_FILE_NAME, "invalidModule"));
    }

    @Test
    void testGetAllQuestionsFromYamlLocaleFileWrongFormat() {
        assertThrows(ConstructorException.class,
                () -> ExamUtils.getAllQuestionsFromYamlLocaleFile(WRONG_FORMAT_FILE_NAME, "invalidModule"));
    }

    @Test
    void testGetAllQuestionsFromYamlLocaleFileWithEmptyQuestion() {
        assertThrows(QuestionLoaderException.class,
                () -> ExamUtils.getAllQuestionsFromYamlLocaleFile(EMPTY_QUESTIONS_FILE_NAME, "invalidModule"));
    }

    @Test
    void testGetAllQuestionsFromYamlMultipartPositive() throws IOException {
        when(mockMultipartFile.getInputStream()).thenReturn(
                new MockMultipartFile("file", validYamlData.getBytes()).getInputStream());

        final List<Question> questions = ExamUtils.getAllQuestionsFromYamlMultipart(mockMultipartFile, MODULE_NAME);
        assertNotNull(questions);
        assertEquals(2, questions.size());
        assertEquals("What is 2 + 2?", questions.get(1).getDescription());
    }

    @Test
    void testGetAllQuestionsFromYamlMultipartNegative() throws IOException {
        when(mockMultipartFile.getInputStream()).thenReturn(
                new MockMultipartFile("empty", new byte[0]).getInputStream());

        assertThrows(RuntimeException.class,
                () -> ExamUtils.getAllQuestionsFromYamlMultipart(mockMultipartFile, MODULE_NAME));
    }

    @Test
    void testGetAllQuestionsFromYamlMultipartIOException() throws IOException {
        when(mockMultipartFile.getInputStream()).thenThrow(IOException.class);

        assertThrows(MultipartFileLoaderException.class,
                () -> ExamUtils.getAllQuestionsFromYamlMultipart(mockMultipartFile, "testModule"));
    }
}
