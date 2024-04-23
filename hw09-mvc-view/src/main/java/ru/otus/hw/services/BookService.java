package ru.otus.hw.services;

import ru.otus.hw.dto.BookDTO;
import ru.otus.hw.models.Book;

import java.util.List;
import java.util.Optional;

public interface BookService {
    Optional<Book> findById(long id);

    List<BookDTO> findAll();

    Book insert(String title, long authorId, long genresIds);

    Book update(long id, String title, long authorId, long genresIds);

    void deleteById(long id);
}
