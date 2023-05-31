package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventUpdateDto;
import ru.practicum.event.model.EventState;
import ru.practicum.event.service.EventServiceImpl;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.EntityNotFoundException;
import ru.practicum.exception.InvalidParameterException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@Validated
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/events")
public class EventAdminController {

    private final EventServiceImpl eventService;

    @GetMapping
    public List<EventFullDto> searchEventsByAdmin(
            @RequestParam(required = false) String users,
            @RequestParam(required = false) String states,
            @RequestParam(required = false) String categories,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
            @Positive @RequestParam(defaultValue = "10") Integer size,
            HttpServletRequest request) {
        log.info("Call#EventAdminController#searchEventsByAdmin# size {} from {} ", size, from);
        return eventService.searchEventsByAdmin(users, states, categories, rangeStart, rangeEnd, size, from, request);
    }


    @PatchMapping("/{eventId}")
    public EventFullDto updateEventByAdmin(@PathVariable @Positive Long eventId,
                                           @Valid @RequestBody EventUpdateDto eventUpdateDto,
                                      HttpServletRequest request) throws EntityNotFoundException, InvalidParameterException, ConflictException {
        log.info("Call#EventAdminController#updateEventByAdmin# eventId: {}, eventUpdateDto: {}", eventId, eventUpdateDto);
        return eventService.updateByAdmin(eventId, eventUpdateDto, request);
    }
}