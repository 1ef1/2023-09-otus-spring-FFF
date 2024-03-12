package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class GenreServiceImpl implements GenreService {
    private final GenreRepository genreRepository;

    private final BookRepository bookRepository;

    @Override
    public List<Genre> findAll() {
        return genreRepository.findAll();
    }

    @Override
    public Genre insert(String name) {
        var genre = new Genre(null, name);
        return genreRepository.save(genre);
    }

    public Genre update(String id, String name) {
        var genreUpdated = new Genre(id, name);
        List<Book> bookDTOList = bookRepository.findByGenreId(id);
        for (Book book : bookDTOList) {
            Book newBook = new Book(book.getId(), book.getTitle(), book.getAuthor(),genreUpdated);
            bookRepository.save(newBook);
        }
        return genreRepository.save(genreUpdated);
    }
}
