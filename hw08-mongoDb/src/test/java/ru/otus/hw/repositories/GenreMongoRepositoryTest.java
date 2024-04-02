package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Genre;

import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("Тесты кастомных методов у жанров")
@DataMongoTest
class GenreMongoRepositoryTest {

    @Autowired
    private GenreRepository repository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    void shouldGenreFindAllByIds() {
        Genre genre1 = new Genre(null, "Genre1");
        mongoTemplate.save(genre1);

        Genre genrePers = repository.findById(genre1.getId()).get();
        assertThat(genre1).isEqualTo(genrePers);
    }
}