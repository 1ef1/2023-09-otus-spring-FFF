package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("Репозиторий JPA для работы с книгами")
@DataJpaTest
@Import({JpaBookRepository.class, JpaAuthorRepository.class, JpaGenreRepository.class})
class JpaBookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private TestEntityManager em;


    @DisplayName("должен загружать книгу по id")
    @Test
    @Order(1)
    void shouldReturnCorrectBookById() throws SQLException {
        Author author = new Author(0, "Author1");
        authorRepository.save(author);

        Genre genre = new Genre(0, "Genre1");
        Genre genreSave = genreRepository.save(genre);

        Book expectedBook = new Book(0, "Title1", author, Collections.singletonList(genre));

        bookRepository.save(expectedBook);
        Book actualBook = bookRepository.findById(expectedBook.getId()).orElse(null);
        assertThat(actualBook).isEqualTo(expectedBook);

         Author expectedAuthor = new Author(2, "Author2");
         authorRepository.save(expectedAuthor);

        assertThat(bookRepository.findAll().size()).isEqualTo(1);
        assertThat(authorRepository.findAll().size()).isEqualTo(2);

        actualBook.setAuthor(expectedAuthor);
        Book b1 = new Book(actualBook.getId(),actualBook.getTitle(),actualBook.getAuthor(),actualBook.getGenres());
        bookRepository.deleteById(actualBook.getId());
        assertThat(bookRepository.findById(b1.getId()).orElse(null)).isEqualTo(null);
        bookRepository.save(b1);
        Book actualBook2 = bookRepository.findById(b1.getId()).orElse(null);

        assertThat(actualBook2.getAuthor()).isEqualTo(expectedAuthor);

        Genre genreUpdate = new Genre(genreSave.getId(), "Genre2");
        Genre genreUpdateResult = genreRepository.save(genreUpdate);
        var v1 = genreRepository.findAllByIds(Set.of(genreUpdateResult.getId()));
    }



}
