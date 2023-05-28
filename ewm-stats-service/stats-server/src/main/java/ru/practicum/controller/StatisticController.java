package ru.practicum.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;
import ru.practicum.service.StatService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@Slf4j
public class StatisticController {

    private final StatService statService;

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    public StatisticController(StatService statService) {
        this.statService = statService;
    }

    @GetMapping("/stats")
    public List<ViewStatsDto> getStats(@RequestParam String start,
                                       @RequestParam String end,
                                       @RequestParam(required = false) List<String> uris,
                                       @RequestParam(defaultValue = "false") Boolean unique) {
        log.info("StatisticController#getStats# start: {}, end: {}, uris: {}, unique: {}", start, end, uris, unique);
        List<ViewStatsDto> viewStatsDtos =  statService.getStatistics(LocalDateTime.parse(start, FORMATTER),
                LocalDateTime.parse(end, FORMATTER),
                uris,
                unique);
        return viewStatsDtos;
    }

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public EndpointHitDto addEvent(@Valid @RequestBody EndpointHitDto endpointHitDto) {
        log.info("StatisticController#addEvent# parameters: {}", endpointHitDto);
        return statService.addEvent(endpointHitDto);
    }
}
