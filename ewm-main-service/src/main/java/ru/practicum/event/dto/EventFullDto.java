package ru.practicum.event.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.category.model.Category;
import ru.practicum.event.location.LocationDto;
import ru.practicum.event.model.EventState;
import ru.practicum.user.dto.UserShortDto;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventFullDto {

    Long id;

    String title;

    String description;

    String annotation;

    EventState state;

    Category category;

    LocalDateTime createdOn;

    String eventDate;

    LocalDateTime publishedOn;

    Integer confirmedRequests;

    LocationDto location;

    UserShortDto initiator;

    boolean paid;

    Integer participantLimit;

    boolean requestModeration;

    Integer views;
}