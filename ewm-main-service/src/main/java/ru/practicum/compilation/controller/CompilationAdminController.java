package ru.practicum.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.dto.CompilationFullDto;
import ru.practicum.compilation.dto.CompilationNewDto;
import ru.practicum.compilation.service.CompilationServiceImpl;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/compilations")
public class CompilationAdminController {

    private final CompilationServiceImpl compilationService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public CompilationFullDto create(@RequestBody @Valid CompilationNewDto compilationNewDto,
                                     HttpServletRequest request) {
        log.info("Received a request to create a new CompilationNewDto: {}", compilationNewDto);
        return compilationService.create(compilationNewDto, request);
    }

    @PatchMapping("/{id}")
    public CompilationFullDto update(@PathVariable Long id, @RequestBody CompilationNewDto compilationNewDto,
                                     HttpServletRequest request) {
        log.info("Received a request to update a compilation with id: {}, CompilationNewDto: {}", id, compilationNewDto);
        return compilationService.update(id, compilationNewDto, request);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        log.info("Received a request to delete a category with id: {}", id);
        compilationService.delete(id);
    }
}