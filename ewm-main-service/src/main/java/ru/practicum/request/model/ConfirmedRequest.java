package ru.practicum.request.model;

import lombok.*;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmedRequest {

    Long eventId;
    Long countConfirmedRequest;
}
