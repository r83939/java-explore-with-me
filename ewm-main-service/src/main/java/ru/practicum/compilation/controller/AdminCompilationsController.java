package ru.practicum.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.ResponseCompilationDto;
import ru.practicum.compilation.service.CompilationsService;
import ru.practicum.exception.InvalidParameterException;


import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/compilations")
public class AdminCompilationsController {
    private final CompilationsService compilationsService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseCompilationDto addCompilation(@Valid @RequestBody NewCompilationDto newCompilationDto) throws InvalidParameterException {
        log.info("Call #AdminCompilationsControllerr#addCompilation# compilationNewDto: {}", newCompilationDto);
        return compilationsService.createCompilation(newCompilationDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable Long id) {
        log.info("Call #AdminCompilationsController#deleteCompilation# CompilationId: {}", id);
        compilationsService.deleteCompilation(id);
    }

    @PatchMapping("/{id}")
    public ResponseCompilationDto updateCompilation(@PathVariable Long id,
                                                    @Valid @RequestBody NewCompilationDto newCompilationDto) {
        log.info("Call #AdminCompilationsControlle#updateCompilation# CompilationId: {}", id);
        return compilationsService.updateCompilation(id, newCompilationDto);
    }
}
