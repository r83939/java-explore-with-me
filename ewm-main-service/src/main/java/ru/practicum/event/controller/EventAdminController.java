package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventUpdateDto;
import ru.practicum.event.service.EventServiceImpl;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.EntityNotFoundException;
import ru.practicum.exception.InvalidParameterException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Validated
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/events")
public class EventAdminController {

    private final EventServiceImpl eventService;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String RANGE_START = "2000-01-01 00:01:01";
    private static final String RANGE_END = "2099-01-01 23:59:59";

    @GetMapping
    public List<EventFullDto> searchEventsByAdmin(
            @RequestParam(defaultValue = "0") List<Long> users,
            @RequestParam(defaultValue = "WAITING, PUBLISHED, CANCELED") List<String> states,
            @RequestParam(defaultValue = "0") List<Integer> categories,
            @RequestParam(defaultValue = RANGE_START) String rangeStart,
            @RequestParam(defaultValue = RANGE_END) String rangeEnd,
            @RequestParam(defaultValue = "10") @Positive Integer size,
            @RequestParam(defaultValue = "1") @PositiveOrZero Integer from,
            HttpServletRequest request) {
        log.info("Call#EventAdminController#searchEventsByAdmin# size {} from {} ", size, from);
        LocalDateTime startTime = LocalDateTime.parse(rangeStart, dateTimeFormatter);
        LocalDateTime endTime = LocalDateTime.parse(rangeEnd, dateTimeFormatter);
        return eventService.searchEventsByAdmin(users, states, categories, startTime, endTime, size, from, request);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventByAdmin(@PathVariable @Positive Long eventId,
                                           @Valid @RequestBody EventUpdateDto eventUpdateDto,
                                      HttpServletRequest request) throws EntityNotFoundException, InvalidParameterException {
        log.info("Call#EventAdminController#updateEventByAdmin# eventId: {}, eventUpdateDto: {}", eventId, eventUpdateDto);
        return eventService.updateByAdmin(eventId, eventUpdateDto, request);
    }
}