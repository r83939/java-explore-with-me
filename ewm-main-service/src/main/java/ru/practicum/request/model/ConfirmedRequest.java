package ru.practicum.request.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmedRequest {
    Long eventId;
    Long countConfirmedRequest;
}
