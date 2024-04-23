package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.otus.hw.models.Genre;

import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("Тесты кастомных методов у жанров")
@DataJpaTest
class GenreSpringDataRepositoryTest {

    @Autowired
    private GenreRepository repository;

    @Test
    void shouldGenreFindAllByIds() {
        Genre genre = repository.findById(1L).get();
        assertThat(genre.getId()).isEqualTo(1L);
    }
}