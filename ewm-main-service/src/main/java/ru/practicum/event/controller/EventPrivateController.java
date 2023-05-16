package ru.practicum.event.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventUpdateDto;
import ru.practicum.event.service.EventService;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventNewDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}/events")
public class EventPrivateController {

    private final EventService eventService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public EventFullDto create(@PathVariable Long userId, @RequestBody @Valid EventNewDto eventNewDto) {
        log.info("Received a request from user with id {} to create a new event: {}", userId, eventNewDto);
        return eventService.create(userId, eventNewDto);
    }

    @GetMapping
    public List<EventFullDto> getByUserId(@PathVariable Long userId,
                                          @RequestParam(defaultValue = "10") @Positive Integer size,
                                          @RequestParam(defaultValue = "0") @Positive Integer from) throws  JsonProcessingException {
        log.info("Received a request to get Events of User with id {} size {} from {}", userId, size, from);
        return eventService.getByUserId(userId, size, from);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getByUserAndEventId(@PathVariable Long userId,
                                            @PathVariable Long eventId,
                                            @RequestParam(defaultValue = "10") @Positive Integer size,
                                            @RequestParam(defaultValue = "0") @Positive Integer from) throws JsonProcessingException {
        log.info("Received a request to get Event id {} of User with id {} size {} from {}", eventId, userId, size, from);
        return eventService.getByUserAndEventId(userId, eventId, size, from);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateByUser(@PathVariable Long userId,
                                     @PathVariable Long eventId,
                                     @RequestBody EventUpdateDto eventUpdateDto) throws  JsonProcessingException {
        log.info("Received a request from User with id {} to update an Event with id: {} EventUpdateDto: {}", userId, eventId, eventUpdateDto);
        return eventService.updateByUser(userId, eventId, eventUpdateDto);
    }
}