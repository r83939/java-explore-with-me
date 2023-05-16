package ru.practicum.compilation.service;

import ru.practicum.compilation.model.CompilationFullDto;
import ru.practicum.compilation.model.CompilationNewDto;

import java.util.List;

public interface CompilationService {
    CompilationFullDto create(CompilationNewDto compilationNewDto);

    List<CompilationFullDto> getAllWithPagination(String pinned, Integer from, Integer size);

    CompilationFullDto get(Long id);

    CompilationFullDto update(Long id, CompilationNewDto compilationNewDto);

    void delete(Long id);
}