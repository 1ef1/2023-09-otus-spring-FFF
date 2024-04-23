package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.BookDTO;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    private final BookRepository bookRepository;

    @Override
    public Optional<Book> findById(long id) {
        return bookRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<BookDTO> findAll() {
        return bookRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Book insert(String title, long authorId, long genresIds) {
        return save(0, title, authorId, genresIds);
    }

    @Override
    @Transactional
    public Book update(long id, String title, long authorId, long genresIds) {
        return save(id, title, authorId, genresIds);
    }

    @Override
    @Transactional
    public void deleteById(long id) {
        bookRepository.deleteById(id);
    }

    private Book save(long id, String title, long authorId, long genresId) {
//        if (isEmpty(genresId)) {
//            throw new IllegalArgumentException("Genres ids must not be null");
//        }
//
        var author = authorRepository.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException("Author with id %d not found".formatted(authorId)));
        var genres = genreRepository.findById(genresId);
//        if (isEmpty(genres) || genresId.size() != genres.size()) {
//            throw new EntityNotFoundException("One or all genres with ids %s not found".formatted(genresId));
//        }

        var book = new Book(id, title, author, genres.get());
        return bookRepository.save(book);
    }

    private BookDTO toDto(Book book) {
        String authorName = book.getAuthor().getFullName();
        String genreName = book.getGenre().getName();

        return new BookDTO(book.getId(), book.getTitle(), authorName, genreName);
    }
}
