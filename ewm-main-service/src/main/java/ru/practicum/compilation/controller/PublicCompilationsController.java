package ru.practicum.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.dto.ResponseCompilationDto;
import ru.practicum.compilation.service.CompilationsService;


import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;


@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/compilations")
public class PublicCompilationsController {
    private final CompilationsService compilationsService;


    @GetMapping
    public List<ResponseCompilationDto> findAll(@RequestParam(required = false) boolean pinned,
                                                @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Call #PublicCompilationsController#findAll# pinned {} size {} from {} ", pinned, size, from);
        PageRequest pageable = PageRequest.of(from / size, size);
        return compilationsService.findAllCompilations(pinned, pageable);
    }

    @GetMapping("/{compId}")
    public ResponseCompilationDto findById(@PathVariable Long compId) {
        log.info("Call #PublicCompilationsControllerr#findById# CompilationId {}", compId);
        return compilationsService.findCompilationsById(compId);
    }
}
