package com.simulator.exam.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.simulator.exam.dto.QuestionsWrapper;
import com.simulator.exam.entity.ModuleEnum;
import com.simulator.exam.entity.Question;
import com.simulator.exam.exception.LocalFileLoaderException;
import com.simulator.exam.exception.LocalFileNotFoundException;
import com.simulator.exam.exception.MultipartFileLoaderException;
import com.simulator.exam.exception.QuestionLoaderException;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

public class ExamUtils {

    @Setter
    @Value("${app.question.file.locale.path}")
    private static String filePath;

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
        if (!StringUtils.hasText(fileName)) {
            LOGGER.log(Level.WARNING, "Error during loading question from file. Filename is null.");
            throw new IllegalArgumentException(
                    String.format("Null filename or missing for file: %s module %s", fileName, module));
        }

        final File file = new File(String.format(filePath, fileName));
        try (final InputStream inputStream = new FileInputStream(file)) {
            return loadQuestionsDataFromYaml(inputStream, module);
        } catch (final FileNotFoundException e) {
            LOGGER.log(Level.WARNING, "File: {} was not found", file.getPath());
            throw new LocalFileNotFoundException(fileName, filePath, e);
        } catch (final IOException e) {
            LOGGER.log(Level.WARNING, "Error during the obtaining of the input stream of local file {}", fileName);
            throw new LocalFileLoaderException(fileName, filePath, module.getModuleName(), e);
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
            LOGGER.log(Level.WARNING, "Error during the obtaining of the input stream of multipart file {}",
                    file.getName());
            throw new MultipartFileLoaderException("Failed to load questions from multipart file", e);
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

        if (questionsWrapper.getQuestions() == null || questionsWrapper.getQuestions().isEmpty()) {
            LOGGER.log(Level.WARNING, "No questions have been read from the yaml file");
            throw new QuestionLoaderException("Exception encountered during the questions loading from YAML file");
        }

        final List<Question> questions = questionsWrapper.getQuestions();
        questions.forEach(question -> question.setModuleEnum(module));
        LOGGER.log(Level.INFO, "{} questions have been successfully read from file", questions.size());
        return questions;
    }
}
