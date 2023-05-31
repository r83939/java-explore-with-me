package ru.practicum.request.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConfirmedRequest {

    Long eventId;
    Long countConfirmedRequest;
}
