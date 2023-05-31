package ru.practicum.Comment.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotEmpty;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentRequestDto {

    @NotEmpty
    Long eventId;

    @NotEmpty
    String text;

}
