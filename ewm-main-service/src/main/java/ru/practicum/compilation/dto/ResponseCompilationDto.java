package ru.practicum.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.event.dto.ShortEventDto;


import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseCompilationDto {
    private Long id;
    private List<ShortEventDto> events;
    private Boolean pinned;
    private String title;
}
