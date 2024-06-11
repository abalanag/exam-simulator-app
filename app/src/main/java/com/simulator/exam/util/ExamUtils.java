package com.simulator.exam.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.simulator.exam.dto.QuestionsWrapper;
import com.simulator.exam.entity.ModuleEnum;
import com.simulator.exam.entity.Question;
import com.simulator.exam.exception.LocalFileLoaderException;
import com.simulator.exam.exception.QuestionParserException;
import org.springframework.web.multipart.MultipartFile;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

public class ExamUtils {

    private static final String FILE_PATH = "src/main/resources/question-files/%s";

    private static final Logger LOGGER = Logger.getLogger("application.logger");

    // Private constructor to prevent instantiation
    private ExamUtils() {
    }

    /**
     * Loads all questions from a local YAML file based on the module.
     *
     * @param module the module enum
     * @return the list of questions
     */
    public static List<Question> getAllQuestionsFromYamlLocaleFile(final String fileName, final ModuleEnum module) {
        final File file = new File(String.format(FILE_PATH, fileName));
        try (final InputStream inputStream = new FileInputStream(file)) {
            return loadQuestionsDataFromYaml(inputStream, module);
        } catch (final IOException e) {
            LOGGER.log(Level.WARNING, String.format("Error reading file: %s: %s", file.getPath(), e.getMessage()));
            throw new LocalFileLoaderException("Failed to load questions from local file", e);
        }
    }

    /**
     * Loads all questions from a YAML file uploaded as a multipart file.
     *
     * @param file   the multipart file
     * @param module the module enum
     * @return the list of questions
     */
    public static List<Question> getAllQuestionsFromYamlMultipart(final MultipartFile file, final ModuleEnum module) {
        try (final InputStream inputStream = file.getInputStream()) {
            return loadQuestionsDataFromYaml(inputStream, module);
        } catch (final IOException e) {
            LOGGER.log(Level.WARNING, String.format("Error reading multipart file: %s", file.getName()), e);
            throw new RuntimeException("Failed to load questions from multipart file", e);
        }
    }

    /**
     * Loads questions data from the provided YAML input stream.
     *
     * @param stream the input stream
     * @param module the module enum
     * @return the list of questions
     */
    private static List<Question> loadQuestionsDataFromYaml(final InputStream stream, final ModuleEnum module) {
        final LoaderOptions options = new LoaderOptions();
        options.setAllowDuplicateKeys(true);

        final Yaml yaml = new Yaml(new Constructor(QuestionsWrapper.class, options));
        final QuestionsWrapper questionsWrapper = yaml.load(stream);

        if (questionsWrapper == null || questionsWrapper.getQuestions() == null) {
            throw new QuestionParserException("Failed to parse questions from YAML");
        }

        final List<Question> questions = questionsWrapper.getQuestions();
        questions.forEach(question -> question.setModuleEnum(module));
        return questions;
    }
}
