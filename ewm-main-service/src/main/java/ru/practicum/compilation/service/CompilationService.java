package ru.practicum.compilation.service;

import ru.practicum.compilation.model.CompilationFullDto;
import ru.practicum.compilation.model.CompilationNewDto;
import ru.practicum.exception.EntityNotFoundException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface CompilationService {
    CompilationFullDto create(CompilationNewDto compilationNewDto, HttpServletRequest request);

    List<CompilationFullDto> getAllWithPagination(String pinned, Integer from, Integer size, HttpServletRequest request);

    CompilationFullDto get(Long id,HttpServletRequest request) throws EntityNotFoundException;

    CompilationFullDto update(Long id, CompilationNewDto compilationNewDto, HttpServletRequest request);

    void delete(Long id);
}