package ru.practicum.comment.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentResponseDto {
    long id;
    long eventId;
    String text;
    String authorName;
    LocalDateTime created;
}
