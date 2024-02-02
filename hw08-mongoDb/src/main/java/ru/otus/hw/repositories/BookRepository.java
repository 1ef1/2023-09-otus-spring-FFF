package ru.otus.hw.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.otus.hw.models.Book;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

    @Override
    @EntityGraph(attributePaths = {"genres","author"})
    Optional<Book> findById(Long id);

    @Override
    @Query("SELECT b FROM Book b JOIN FETCH b.author")
    List<Book> findAll();

    @Override
    void deleteById(Long id);

}
