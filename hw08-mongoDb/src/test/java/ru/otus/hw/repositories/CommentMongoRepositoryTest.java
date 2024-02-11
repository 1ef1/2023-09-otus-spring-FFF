package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("Тесты кастомных методов у комментариев")
@DataMongoTest
class CommentMongoRepositoryTest {

    @Autowired
    private CommentRepository repository;

    @Test
    void shouldCommentFindAllByBookId() {
        int actualSize = repository.findAll().size();
        assertThat(actualSize).isEqualTo(2);
    }
}