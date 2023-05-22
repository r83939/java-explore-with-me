package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.service.EventServiceImpl;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/events")
public class EventPublicController {

    private final EventServiceImpl eventService;


    @GetMapping
    public List<EventFullDto> searchEventsPublic(
            @RequestParam(defaultValue = "") String text,
            @RequestParam(defaultValue = "false") String paid,
            @RequestParam(defaultValue = "empty") String rangeStart,
            @RequestParam(defaultValue = "empty") String rangeEnd,
            @RequestParam(defaultValue = "false") String onlyAvailable,
            //@RequestParam(defaultValue = 0) List<Integer> categories,
            @RequestParam(required = false) List<Integer> categories,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "10") @Positive Integer size,
            @RequestParam(defaultValue = "0") @Positive Integer from,
            HttpServletRequest request) {

        log.info("Call#EventPublicController#getEventByEventId# text: {}, paid: {}, rangeStart: {}, rangeEnd: {}, " +
                "onlyAvailable: {}, sort: {}", text, paid, rangeStart, rangeEnd, onlyAvailable, sort);

        return eventService.searchEventsPublic(text, Boolean.parseBoolean(paid), rangeStart, rangeEnd,
                Boolean.getBoolean(onlyAvailable), categories, sort, size, from, request);
    }

    @GetMapping("/{id}")
    public EventFullDto getEventByEventId(@PathVariable Long id,
                                     HttpServletRequest request) {
        log.info("Call#EventPublicController#getEventByEventId# eventId: {}", id);
        return eventService.getByEventId(id, request);
    }
}