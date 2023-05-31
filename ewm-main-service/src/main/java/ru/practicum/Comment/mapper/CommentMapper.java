package ru.practicum.Comment.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.Comment.dto.CommentRequestDto;
import ru.practicum.Comment.dto.CommentResponseDto;
import ru.practicum.Comment.model.Comment;


@Component
public class CommentMapper {
    public static CommentResponseDto toCommentResponceDto(Comment comment) {
        return new CommentResponseDto(
                comment.getId(),
                comment.getText(),
                comment.getUser().getName(),
                comment.getCreated()
        );
    }

}
