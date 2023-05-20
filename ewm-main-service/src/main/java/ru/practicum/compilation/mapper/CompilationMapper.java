package ru.practicum.compilation.mapper;

import lombok.NoArgsConstructor;
import ru.practicum.compilation.dto.CompilationFullDto;
import ru.practicum.compilation.dto.CompilationNewDto;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.event.dto.EventShortDto;

import java.util.List;

@NoArgsConstructor
public final class CompilationMapper {

    public static CompilationFullDto toCompilationFullDtoFromCompilation(Compilation compilation, List<EventShortDto> events) {
        return new CompilationFullDto(
                compilation.getId(),
                compilation.getTitle(),
                compilation.getPinned(),
                events
        );
    }

    public static Compilation toCompilationFromCompilationNewDto(CompilationNewDto compilationNewDto) {
        return new Compilation(
                compilationNewDto.getId(),
                compilationNewDto.getTitle(),
                compilationNewDto.getPinned()
        );
    }
}