package ru.practicum.compilation.mapper;


import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.ResponseCompilationDto;

import ru.practicum.compilation.model.Compilation;
import ru.practicum.event.dto.ShortEventDto;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;

import java.util.List;
import java.util.stream.Collectors;

public class CompilationMapper {
    public static Compilation toCompilation(NewCompilationDto newCompilationDto, List<Event> events) {
        return new Compilation(null,
                events,
                newCompilationDto.getPinned(),
                newCompilationDto.getTitle());
    }

    public static ResponseCompilationDto toResponseCompilationDto(Compilation compilation) {
        List<ShortEventDto> shortEvents = compilation.getEvents().stream()
                .map(e -> EventMapper.toShortEventDto(e))
                .collect(Collectors.toList());
        return new ResponseCompilationDto(compilation.getId(),
                shortEvents,
                compilation.getPinned(),
                compilation.getTitle());
    }
}
