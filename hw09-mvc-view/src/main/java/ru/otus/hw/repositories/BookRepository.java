package ru.otus.hw.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.otus.hw.models.Book;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {
    @Override
    @Query("SELECT b FROM Book b JOIN FETCH b.author JOIN FETCH b.genre")
    List<Book> findAll();
}
