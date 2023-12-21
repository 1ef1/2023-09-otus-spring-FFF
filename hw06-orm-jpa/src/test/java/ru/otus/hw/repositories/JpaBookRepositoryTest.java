package ru.otus.hw.repositories;

import org.h2.tools.Console;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//import static ru.otus.hw.repositories.JdbcBookRepositoryTest.*;

@DisplayName("Репозиторий JPA для работы с книгами")
@DataJpaTest
@Import({JpaBookRepository.class, JpaAuthorRepository.class, JpaGenreRepository.class})
class JpaBookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private TestEntityManager em;

    private List<Author> dbAuthors;
    private List<Genre> dbGenres;
    private List<Book> dbBooks;

    @BeforeEach
//    void setUp() {
//        dbAuthors = getDbAuthors();
//        dbGenres = getDbGenres();
//        dbBooks = getDbBooks(dbAuthors, dbGenres);
//    }

    @DisplayName("должен загружать книгу по id")
    @Test
    void shouldReturnCorrectBookById() throws SQLException {
//        Book expectedBook = dbBooks.get(0); // Получаем первую книгу для теста
//        Console.main("-browser");
        Author author = new Author(0,"Author1");
        authorRepository.save(author);
        Book expectedBook = new Book(0,"Title1",author, Collections.emptyList());
        bookRepository.save(expectedBook);
        Book actualBook = bookRepository.findById(expectedBook.getId()).orElse(null);
        assertThat(actualBook).isEqualTo(expectedBook);
    }
}
