package ru.practicum.event.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.category.model.Category;
import ru.practicum.user.dto.UserShortDto;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventShortDto {

    Long id;

    String title;

    String description;

    String annotation;

    Category category;

    String eventDate;

    UserShortDto initiator;

    boolean paid;

    Long views;
}