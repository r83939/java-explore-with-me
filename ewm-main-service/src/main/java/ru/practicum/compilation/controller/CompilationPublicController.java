package ru.practicum.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.service.CompilationServiceImpl;
import ru.practicum.compilation.model.CompilationFullDto;

import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/compilations")
public class CompilationPublicController {

    private final CompilationServiceImpl compilationService;

    @GetMapping
    public List<CompilationFullDto> getAllWithPagination(
            @RequestParam(defaultValue = "false") String pinned,
            @RequestParam(defaultValue = "0") @Positive Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Received a request to get Compilations pinned {} size {} from {} ", pinned, size, from);
        return compilationService.getAllWithPagination(pinned, size, from);
    }

    @GetMapping("/{compId}")
    public CompilationFullDto get(@PathVariable Long compId) {
        log.info("Received a request to get Compilation with id {} ", compId);
        return compilationService.get(compId);
    }
}