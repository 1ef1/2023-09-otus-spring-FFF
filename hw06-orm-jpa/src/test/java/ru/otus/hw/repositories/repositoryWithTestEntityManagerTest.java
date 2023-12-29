package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.junit.jupiter.api.Test;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.util.Collections;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
public class repositoryWithTestEntityManagerTest {
    @Autowired
    private TestEntityManager em;

    @DisplayName("производит проверку работу репозиториев")
    @Test
    void shouldReturnCorrectBookById() {
        Author author = new Author(0, "Author1");
        em.persistAndFlush(author);

        Genre genre = new Genre(0, "Genre1");
        em.persistAndFlush(genre);

        Book expectedBook = new Book(0, "Title1", author, Collections.singletonList(genre));
        em.persistAndFlush(expectedBook);

        Book actualBook = em.find(Book.class, expectedBook.getId());
        assertThat(actualBook).isEqualTo(expectedBook);

        Author expectedAuthor = new Author(0, "Author2");
        em.persistAndFlush(expectedAuthor);

        assertThat(em.getEntityManager().createQuery("SELECT b FROM Book b").getResultList().size()).isEqualTo(1);
        assertThat(em.getEntityManager().createQuery("SELECT a FROM Author a").getResultList().size()).isEqualTo(2);

        actualBook.setAuthor(expectedAuthor);
        em.persistAndFlush(actualBook);

        Book actualBook2 = em.find(Book.class, actualBook.getId());
        assertThat(actualBook2.getAuthor()).isEqualTo(expectedAuthor);
        assertThat(em.find(Author.class, expectedAuthor.getId())).isEqualTo(expectedAuthor);

        Genre genreUpdate = new Genre(genre.getId(), "Genre2");
        em.merge(genreUpdate);
        em.flush();

        Genre genreUpdateResult = em.find(Genre.class, genreUpdate.getId());
        assertThat(genreUpdateResult.getName()).isEqualTo("Genre2");

        if (!em.getEntityManager().contains(actualBook2)) {
            actualBook2 = em.merge(actualBook2);
        }
        em.remove(actualBook2);
        em.flush();
        assertThat(em.find(Book.class, actualBook2.getId())).isNull();
    }
}
