package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Репозиторий Mongo для работы с книгами")
@DataMongoTest
class BookMongoRepositoryTest {

    @Autowired
    private BookRepository repository;

    @Autowired
    private MongoTemplate mongoTemplate; // используется вместо TestEntityManager

    @Test
    void savedBookShouldPersist() {
        Author author = new Author("0", "Author1");
        mongoTemplate.save(author);

        Genre genre = new Genre("0", "Genre1");
        Book expectedBook = new Book("0", "Title1", author, Collections.singletonList(genre));

        repository.save(expectedBook);

        Book actualBook = mongoTemplate.findById(expectedBook.getId(), Book.class);
        assertThat(actualBook).isEqualTo(expectedBook);
    }

    @Test
    void shouldFindBookById() {
        final Author author = new Author("0", "testAuthor");
        mongoTemplate.save(author);

        final Genre genre = new Genre("0", "testGenre");
        mongoTemplate.save(genre);

        final Book expectedBook = new Book("0", "testBook", author, Collections.singletonList(genre));
        mongoTemplate.save(expectedBook);

        Optional<Book> actualBookOptional = repository.findById(expectedBook.getId());

        assertTrue(actualBookOptional.isPresent());
        assertThat(actualBookOptional.get()).isEqualTo(expectedBook);
    }

    @Test
    void shouldFindAllBooks() {
        Author testAuthor1 = new Author("0", "testAuthor1");
        Author testAuthor2 = new Author("0", "testAuthor2");
        mongoTemplate.save(testAuthor1);
        mongoTemplate.save(testAuthor2);

        final Genre testGenre1 = new Genre("0", "testGenre1");
        final Genre testGenre2 = new Genre("0", "testGenre2");

        final Book firstExpectedBook = new Book("0", "testBook1", testAuthor1,
                Collections.singletonList(testGenre1));
        final Book secondExpectedBook = new Book("0", "testBook2", testAuthor2,
                Collections.singletonList(testGenre2));

        mongoTemplate.save(firstExpectedBook);
        mongoTemplate.save(secondExpectedBook);

        List<Book> actualBooks = repository.findAll();

        assertThat(actualBooks.size()).isEqualTo(5);
    }

    @Test
    void shouldDeleteBookById() {
        final Genre genre = new Genre("0", "testGenre");
        Author testAuthor = new Author("0", "testAuthor");
        mongoTemplate.save(testAuthor);
        final Book firstBook = new Book("0", "testBook", testAuthor,
                Collections.singletonList(genre));
        Book savedBook = mongoTemplate.save(firstBook);
        repository.deleteById(savedBook.getId());
        Book deletedBook = mongoTemplate.findById(savedBook.getId(), Book.class);
        assertNull(deletedBook);
    }
}
