package ru.practicum.event.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.event.location.LocationDto;
import ru.practicum.event.model.StateAction;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateEventUserRequestDto {

    String annotation;
    Integer category;
    String description;
    String eventDate;
    LocationDto location;
    boolean paid;
    Integer participantLimit;
    boolean requestModeration;
    StateAction stateAction;
    String title;

}
