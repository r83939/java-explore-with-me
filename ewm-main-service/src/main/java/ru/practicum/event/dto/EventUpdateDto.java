package ru.practicum.event.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.event.location.Location;
import ru.practicum.event.model.StateAction;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventUpdateDto {

    Long id;

    @NotEmpty
    @Size(max = 120)
    @Size(min = 3)
    String title;

    @NotEmpty
    @Size(max = 7000)
    @Size(min = 20)
    String description;

    @NotEmpty
    @Size(max = 2000)
    @Size(min = 20)
    String annotation;

    Integer category;

    String eventDate;

    Location location;

    String paid;

    Integer participantLimit;

    String requestModeration;

    StateAction stateAction;
}