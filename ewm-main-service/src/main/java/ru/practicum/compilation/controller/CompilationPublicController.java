package ru.practicum.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.dto.CompilationFullDto;
import ru.practicum.compilation.service.CompilationServiceImpl;
import ru.practicum.exception.EntityNotFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/compilations")
public class CompilationPublicController {

    private final CompilationServiceImpl compilationService;

    @GetMapping
    public List<CompilationFullDto> getAllCompilationsWithPagination(
            @RequestParam(defaultValue = "false") String pinned,
            @RequestParam(defaultValue = "0") @Positive Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size,
            HttpServletRequest request) {
        log.info("Call #CompilationPublicController#getAllCompilationsWithPagination# pinned {} size {} from {} ", pinned, size, from);
        return compilationService.getAllWithPagination(pinned, size, from, request);
    }

    @GetMapping("/{compId}")
    public CompilationFullDto getCompilation(@PathVariable Long compId,
                                  HttpServletRequest request) throws EntityNotFoundException {
        log.info("Call #CompilationPublicController#getCompilation# CompilationId {}", compId);
        return compilationService.getCompilation(compId, request);
    }
}