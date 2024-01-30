package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("Тесты кастомных методов у жанров")
@DataJpaTest
class GenreSpringDataRepositoryTest {

    @Autowired
    private GenreRepository repository;

    @Test
    void shouldGenreFindAllByIds() {
        int actualSize = repository.findAllByIds(Set.of(1L,2L,3L)).size();
        assertThat(actualSize).isEqualTo(3);
    }
}