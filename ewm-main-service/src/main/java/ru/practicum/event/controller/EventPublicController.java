package ru.practicum.event.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.model.Sort;
import ru.practicum.event.service.EventServiceImpl;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.EntityNotFoundException;
import ru.practicum.exception.InvalidParameterException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/events")
public class EventPublicController {

    private final EventServiceImpl eventService;

    @GetMapping
    public List<EventFullDto> searchEventsPublic(
                                         @RequestParam(required = false) String text,
                                         @RequestParam(required = false) List<Long> categories,
                                         @RequestParam(required = false) Boolean paid,
                                         @RequestParam(required = false) String rangeStart,
                                         @RequestParam(required = false) String rangeEnd,
                                         @RequestParam(required = false) Boolean onlyAvailable,
                                         @RequestParam(required = false) Sort sort,
                                         @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                         @Positive @RequestParam(defaultValue = "10") Integer size,
                                         HttpServletRequest request) throws InvalidParameterException, IOException, ConflictException {
        log.info("Call#EventPublicController#searchEventsPublic# text: {}, categories: {}, paid: {}, rangeStart: {}, rangeEnd: {}, " +
                "onlyAvailable: {}, sort: {}, from: {}, size: {}", text, paid, rangeStart, rangeEnd, onlyAvailable, sort);
        return eventService.searchEventsPublic(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size, request);
    }

    @GetMapping("/{id}")
    public EventFullDto getEventByEventId(@PathVariable @Positive Long id,
                                     HttpServletRequest request) throws EntityNotFoundException {
        log.info("Call#EventPublicController#getEventByEventId# eventId: {}", id);
        return eventService.getByEventId(id, request);
    }
}