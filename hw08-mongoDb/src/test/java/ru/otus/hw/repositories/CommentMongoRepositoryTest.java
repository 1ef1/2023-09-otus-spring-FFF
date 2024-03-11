package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;

import java.util.Collections;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("Тесты кастомных методов у комментариев")
@DataMongoTest
class CommentMongoRepositoryTest {

    @Autowired
    private CommentRepository repository;

    @Autowired
    private MongoTemplate mongoTemplate;
    @Test
    void shouldCommentFindAllByBookId() {
        Author author = new Author(null, "Author1");
//        mongoTemplate.save(author);

        Genre genre = new Genre(null, "Genre1");
        Book expectedBook = new Book(null, "Title1", author, genre);
        mongoTemplate.save(expectedBook);
        Comment comment = new Comment(null,expectedBook,"testComment");
        mongoTemplate.save(comment);
        int actualSize = repository.findAll().size();
        assertThat(actualSize).isEqualTo(1);
    }
}