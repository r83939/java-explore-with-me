package ru.practicum.event.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventNewDto;
import ru.practicum.event.dto.EventUpdateDto;
import ru.practicum.event.service.EventService;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.EntityNotFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}/events")
public class EventUserController {

    private final EventService eventService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public EventFullDto addEvent(@PathVariable Long userId, @RequestBody @Valid EventNewDto eventNewDto) throws ConflictException, EntityNotFoundException {
        log.info("Call#EventUserController#addEvent# userId: {}, eventNewDto: {}", userId, eventNewDto);
        return eventService.create(userId, eventNewDto);
    }

    @GetMapping
    public List<EventFullDto> getEventsByUserId(@PathVariable Long userId,
                                          @RequestParam(defaultValue = "10") @Positive Integer size,
                                          @RequestParam(defaultValue = "0") @Positive Integer from,
                                          HttpServletRequest request) throws JsonProcessingException {
        log.info("Call#EventUserController#getEventsByUserId# userId: {}, size: {}, from: {}", userId, size, from);
        return eventService.getByUserId(userId, size, from, request);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventByUserAndEventId(@PathVariable Long userId,
                                            @PathVariable Long eventId,
                                            @RequestParam(defaultValue = "10") @Positive Integer size,
                                            @RequestParam(defaultValue = "0") @Positive Integer from,
                                            HttpServletRequest request) throws  JsonProcessingException {
        log.info("Call#EventUserController#getEventByUserAndEventId# eventId: {}, userId: {}, size: {}, from: {}", eventId, userId, size, from);
        return eventService.getByUserAndEventId(userId, eventId, size, from, request);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventByUser(@PathVariable Long userId,
                                     @PathVariable Long eventId,
                                     @RequestBody EventUpdateDto eventUpdateDto,
                                     HttpServletRequest request) throws JsonProcessingException, EntityNotFoundException {
        log.info("Call#EventUserController#addEvent# userId: {},eventId: {}, eventNewDto: {}", userId,eventId, eventUpdateDto);
        return eventService.updateByUser(userId, eventId, eventUpdateDto, request);
    }
}