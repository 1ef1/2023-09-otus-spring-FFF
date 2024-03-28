package ru.otus.hw.services;

import ru.otus.hw.dto.BookDTO;
import ru.otus.hw.models.Book;

import java.util.List;
import java.util.Optional;

public interface BookService {
    Optional<Book> findById(String id);

    List<BookDTO> findAll();



    Book insert(String title, String authorId, String genresIds);

    Book update(String id, String title, String authorId, String genresIds);

    void deleteById(String id);
}
