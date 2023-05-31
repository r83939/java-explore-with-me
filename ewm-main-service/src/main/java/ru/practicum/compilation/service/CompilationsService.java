package ru.practicum.compilation.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.ResponseCompilationDto;
import ru.practicum.exception.InvalidParameterException;

import java.util.List;

public interface CompilationsService {

    ResponseCompilationDto createCompilation(NewCompilationDto newCompilationDto) throws InvalidParameterException;

    void deleteCompilation(Long compId);

    ResponseCompilationDto updateCompilation(Long compId, NewCompilationDto newCompilationDto);

    List<ResponseCompilationDto> findAllCompilations(Boolean pinned, Pageable pageable);

    ResponseCompilationDto findCompilationsById(Long compId);
}

