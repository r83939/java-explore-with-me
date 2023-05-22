package ru.practicum.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.dto.CompilationFullDto;
import ru.practicum.compilation.dto.CompilationNewDto;
import ru.practicum.compilation.service.CompilationServiceImpl;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Validated
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/compilations")
public class CompilationAdminController {

    private final CompilationServiceImpl compilationService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public CompilationFullDto addCompilation(@RequestBody @Valid CompilationNewDto compilationNewDto,
                                     HttpServletRequest request) {
        log.info("Call #CompilationAdminController#addCompilation# compilationNewDto: {}", compilationNewDto);
        return compilationService.addCompilation(compilationNewDto, request);
    }

    @PatchMapping("/{id}")
    public CompilationFullDto updateCompilation(@PathVariable Long id, @RequestBody CompilationNewDto compilationNewDto,
                                     HttpServletRequest request) {
        log.info("Call #CompilationAdminController#addCompilation# CompilationId: {}", id);
        return compilationService.updateCompilation(id, compilationNewDto, request);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteCompilation(@PathVariable Long id) {
        log.info("Call #CompilationAdminController#deleteCompilation# CompilationId: {}", id);
        compilationService.deleteCompilation(id);
    }
}