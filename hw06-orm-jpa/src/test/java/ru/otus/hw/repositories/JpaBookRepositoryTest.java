package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Репозиторий JPA для работы с книгами")
@DataJpaTest
@Import({JpaBookRepository.class})
class JpaBookRepositoryTest {

    @Autowired
    private BookRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void savedBookShouldPersist() {
        Author author = new Author(0, "Author1");
        entityManager.persistAndFlush(author);

        Genre genre = new Genre(0, "Genre1");
        Book expectedBook = new Book(0, "Title1", author, Collections.singletonList(genre));

        repository.save(expectedBook);

        Book actualBook = entityManager.find(Book.class, expectedBook.getId());
        assertThat(actualBook).isEqualTo(expectedBook);
    }

    @Test
    void shouldFindBookById() {
        final Author author = new Author(0L, "testAuthor");
        entityManager.persist(author);

        final Genre genre = new Genre(0L, "testGenre");
        entityManager.persist(genre);

        final Book expectedBook = new Book(0L, "testBook", author, Collections.singletonList(genre));
        Book savedBook = entityManager.persist(expectedBook);
        entityManager.flush();

        Optional<Book> actualBookOptional = repository.findById(savedBook.getId());

        assertTrue(actualBookOptional.isPresent());
        assertEquals(expectedBook, actualBookOptional.get());
    }

    @Test
    void shouldFindAllBooks() {
        Author testAuthor1 = new Author(0, "testAuthor1");
        Author testAuthor2 = new Author(0, "testAuthor2");
        entityManager.persistAndFlush(testAuthor1);
        entityManager.persistAndFlush(testAuthor2);

        final Genre testGenre1 = new Genre(0L, "testGenre1");
        entityManager.persist(testGenre1);
        final Genre testGenre2 = new Genre(0L, "testGenre2");
        entityManager.persist(testGenre2);

        final Book firstExpectedBook = new Book(0L, "testBook1", testAuthor1,
                Collections.singletonList(testGenre1));
        final Book secondExpectedBook = new Book(0L, "testBook2", testAuthor2,
                Collections.singletonList(testGenre2));

        entityManager.persist(firstExpectedBook);
        entityManager.persist(secondExpectedBook);

        List<Book> actualBooks = repository.findAll();

        assertEquals(2, actualBooks.size());
    }

    @Test
    void shouldDeleteBookById() {
        final Genre genre = new Genre(0L, "testGenre");
        Author testAuthor = new Author(0, "testAuthor");
        entityManager.persistAndFlush(testAuthor);
        final Book firstBook = new Book(0L, "testBook", testAuthor,
                Collections.singletonList(genre));
        Book savedBook = entityManager.persist(firstBook);
        repository.deleteById(savedBook.getId());
        Book deletedBook = entityManager.find(Book.class, savedBook.getId());
        assertNull(deletedBook);
    }
}