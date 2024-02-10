package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.models.Author;
import ru.otus.hw.repositories.AuthorRepository;

import java.util.List;


@RequiredArgsConstructor
@Service
public class AuthorServiceImpl implements AuthorService {
    private final AuthorRepository authorRepository;

    @Override
    public List<Author> findAll() {
        return authorRepository.findAll();
    }

    @Override
    public Author insert(String fullName) {
        var author1 = new Author("0", fullName);
        return authorRepository.save(author1);
    }

    private Author save(String id, String fullName) {


        var author = new Author(id, fullName);
        return authorRepository.save(author);
    }

}
