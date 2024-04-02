package ru.otus.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;

@Repository
@RequiredArgsConstructor
class AuthorRepositoryCustomImpl implements AuthorRepositoryCustom {

    private final MongoTemplate mongoTemplate;

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
