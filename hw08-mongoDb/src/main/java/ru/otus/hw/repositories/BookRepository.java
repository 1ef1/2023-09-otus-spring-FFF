package ru.otus.hw.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.hw.models.Book;

import java.util.List;

public interface BookRepository extends MongoRepository<Book, String> {

    @Override
    List<Book> findAll();

    List<Book> findByAuthorId(String id);

    List<Book> findByGenreId(String id);

    @Override
    void deleteById(String id);

}
