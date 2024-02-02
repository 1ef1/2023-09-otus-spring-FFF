package ru.otus.hw.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;


@Document(collection = "comments")
@Data
@ToString(exclude = "book")
@EqualsAndHashCode(exclude = "book")
@AllArgsConstructor
@NoArgsConstructor
public class Comment {

    @Id
    private String id;

    @DBRef(lazy = true)
    private Book book;

    @Field("comment_text")
    private String commentText;
}
