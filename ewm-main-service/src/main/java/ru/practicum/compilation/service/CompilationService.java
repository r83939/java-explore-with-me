package ru.practicum.compilation.service;

import ru.practicum.compilation.dto.CompilationFullDto;
import ru.practicum.compilation.dto.CompilationNewDto;
import ru.practicum.exception.EntityNotFoundException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface CompilationService {
    CompilationFullDto addCompilation(CompilationNewDto compilationNewDto, HttpServletRequest request);

    List<CompilationFullDto> getAllWithPagination(String pinned, Integer from, Integer size, HttpServletRequest request);

    CompilationFullDto getCompilation(Long id,HttpServletRequest request) throws EntityNotFoundException;

    CompilationFullDto updateCompilation(Long id, CompilationNewDto compilationNewDto, HttpServletRequest request);

    void deleteCompilation(Long id);
}