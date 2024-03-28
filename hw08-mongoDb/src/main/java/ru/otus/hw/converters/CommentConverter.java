package ru.otus.hw.converters;

import org.springframework.stereotype.Component;
import ru.otus.hw.models.Comment;

@Component
public class CommentConverter {
    public static String commentToString(Comment comment) {
        return "Id: %d, BookId: %s, CommentText: %s".formatted(comment.getId(), comment.getBook().getId()
                , comment.getCommentText());
    }
}
