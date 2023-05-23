package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.service.EventServiceImpl;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.InvalidParameterException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
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
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            HttpServletRequest request) throws InvalidParameterException {

        log.info("Call#EventPublicController#searchEventsPublic# text: {}, paid: {}, rangeStart: {}, rangeEnd: {}, " +
                "onlyAvailable: {}, sort: {}", text, paid, rangeStart, rangeEnd, onlyAvailable, sort);

        return eventService.searchEventsPublic(text, Boolean.parseBoolean(paid), rangeStart, rangeEnd,
                Boolean.getBoolean(onlyAvailable), categories, sort, size, from, request);
    }

    @GetMapping
    public List<EventFullDto> findEvents(@RequestParam(required = false) List<Long> users,
                                         @RequestParam(required = false) List<Long> categories,
                                         @RequestParam(required = false) String rangeStart,
                                         @RequestParam(required = false) String rangeEnd,
                                         @RequestParam(required = false) String onlyAvailable,
                                         @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                         @Positive @RequestParam(defaultValue = "10") Integer size,
                                         HttpServletRequest request) {
        return eventService.searchEventsPublic1(users, categories, rangeStart, rangeEnd, from, size, request);
    }

    @GetMapping("/{id}")
    public EventFullDto getEventByEventId(@PathVariable @Positive Long id,
                                     HttpServletRequest request) {
        log.info("Call#EventPublicController#getEventByEventId# eventId: {}", id);
        return eventService.getByEventId(id, request);
    }
}