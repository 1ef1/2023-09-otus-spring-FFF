package ru.otus.hw.dao;

import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import ru.otus.hw.Application;
import ru.otus.hw.config.AppConfig;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.dao.dto.QuestionDto;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class CsvQuestionDao implements QuestionDao {
    private final TestFileNameProvider fileNameProvider;

    @Override
    public List<Question> findAll() {
        // Использовать CsvToBean
        // https://opencsv.sourceforge.net/#collection_based_bean_fields_one_to_many_mappings
        // Использовать QuestionReadException

        ClassLoader classLoader = Application.class.getClassLoader();

        List<QuestionDto> questions;
        AppConfig appConfig = new AppConfig("questions.csv");
        InputStream inStream = Objects.requireNonNull(classLoader.getResourceAsStream(appConfig.getTestFileName()));
        try (Reader fileReader = new InputStreamReader(inStream)) {

            questions = new CsvToBeanBuilder<QuestionDto>(fileReader)
                    .withType(QuestionDto.class)
                    .withSkipLines(1)
                    .withSeparator(';')
                    .build()
                    .parse();

        } catch (IOException e) {
            throw new QuestionReadException("CsvQuestionDao Exception", e);
        }
        return questions.stream().map(QuestionDto::toDomainObject).collect(Collectors.toList());
    }
}
