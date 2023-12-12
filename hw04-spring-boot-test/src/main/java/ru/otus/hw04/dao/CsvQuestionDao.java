package ru.otus.hw04.dao;

import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.otus.hw04.config.TestFileNameProvider;
import ru.otus.hw04.dao.dto.QuestionDto;
import ru.otus.hw04.domain.Question;
import ru.otus.hw04.exceptions.QuestionReadException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class CsvQuestionDao implements QuestionDao {
    private final TestFileNameProvider fileNameProvider;

    @Override
    public List<Question> findAll() {
        // Использовать CsvToBean
        // https://opencsv.sourceforge.net/#collection_based_bean_fields_one_to_many_mappings
        // Использовать QuestionReadException

        ClassLoader classLoader = CsvQuestionDao.class.getClassLoader();


        InputStream iS = Objects.requireNonNull(classLoader
                .getResourceAsStream(this.fileNameProvider.getTestFileName()));
        try (Reader fileReader = new InputStreamReader(iS)) {
            List<QuestionDto> questions = new CsvToBeanBuilder<QuestionDto>(fileReader)
                    .withType(QuestionDto.class)
                    .withSkipLines(1)
                    .withSeparator(';')
                    .build()
                    .parse();
            return questions.stream().map(QuestionDto::toDomainObject).collect(Collectors.toList());

        } catch (IOException e) {
            throw new QuestionReadException("CsvQuestionDao Exception", e);
        }
    }
}
