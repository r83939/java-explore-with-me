package ru.practicum.request.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.request.model.RequestState;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RequestDto {

    Long id;

    LocalDateTime created;

    Long event;

    Long requester;

    RequestState status;
}