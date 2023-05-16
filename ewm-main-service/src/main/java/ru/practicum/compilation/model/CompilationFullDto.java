package ru.practicum.compilation.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.event.dto.EventShortDto;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompilationFullDto {

    Long id;

    String title;

    String pinned;

    List<EventShortDto> events;
}