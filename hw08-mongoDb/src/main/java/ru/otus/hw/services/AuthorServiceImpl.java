package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.repositories.AuthorRepository;

import java.util.List;


@RequiredArgsConstructor
@Service
public class AuthorServiceImpl implements AuthorService {
    private final AuthorRepository authorRepository;

    private final MongoTemplate mongoTemplate;


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
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(id));

        Update update = new Update();
        update.set("fullName", fullName);

        mongoTemplate.updateFirst(query, update, Author.class);

        Update updateAuthor = new Update();
        updateAuthor.set("author.fullName", fullName);
        mongoTemplate.updateMulti(new Query(Criteria.where("author.id").is(id)), updateAuthor, Book.class);

        return mongoTemplate.findOne(query, Author.class);
    }
}
