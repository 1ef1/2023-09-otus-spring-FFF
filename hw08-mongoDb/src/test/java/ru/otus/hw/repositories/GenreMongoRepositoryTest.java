package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("Тесты кастомных методов у жанров")
@DataMongoTest
class GenreMongoRepositoryTest {

    @Autowired
    private GenreRepository repository;

    @Test
    void shouldGenreFindAllByIds() {
        int actualSize = repository.findByIdIn(Set.of("1","2","3")).size();
        assertThat(actualSize).isEqualTo(3);
    }
}