package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("Тесты кастомных методов у комментариев")
@DataJpaTest
class CommentSpringDataRepositoryTest {

    @Autowired
    private CommentRepository repository;

    @Test
    void shouldCommentFindAllByBookId() {
        int actualSize = repository.findAllByBookId(1L).size();
        assertThat(actualSize).isEqualTo(3);
    }
}