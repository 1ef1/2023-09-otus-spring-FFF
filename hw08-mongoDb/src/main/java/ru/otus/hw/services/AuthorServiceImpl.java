package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import java.util.List;


@RequiredArgsConstructor
@Service
public class AuthorServiceImpl implements AuthorService {
    private final AuthorRepository authorRepository;

    private final BookRepository bookRepository;

    @Override
    public List<Author> findAll() {
        return authorRepository.findAll();
    }

    @Override
    public Author insert(String fullName) {
        var author1 = new Author(null, fullName);
        return authorRepository.save(author1);
    }

    @Override
    public Author update(String id, String fullName) {
        var author1 = new Author(id, fullName);
        List<Book> bookDTOList = bookRepository.findByAuthorId(id);
        for (Book book : bookDTOList) {
            bookRepository.save(new Book(book.getId(), book.getTitle(), author1,book.getGenre()));
        }
        return authorRepository.save(author1);
    }
}
