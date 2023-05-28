package ru.practicum.event.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventNewDto;
import ru.practicum.event.dto.EventUpdateDto;
import ru.practicum.event.model.Sort;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.EntityNotFoundException;
import ru.practicum.exception.InvalidParameterException;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    EventFullDto addEvent(Long id, EventNewDto eventNewDto) throws ConflictException, EntityNotFoundException, InvalidParameterException;

    List<EventFullDto> searchEventsPublic(String text,
                                           List<Long> categories,
                                           Boolean paid,
                                           String rangeStart,
                                           String rangeEnd,
                                           Boolean onlyAvailable,
                                           Sort sort,
                                           Integer from,
                                           Integer size,
                                           HttpServletRequest request) throws IOException, ConflictException, InvalidParameterException;
    EventFullDto getByEventId(Long id, HttpServletRequest request) throws EntityNotFoundException;

    List<EventFullDto> getByUserId(Long id, Integer size, Integer from, HttpServletRequest request) throws JsonProcessingException;

    EventFullDto getByUserAndEventId(Long userId, Long eventId, HttpServletRequest request) throws JsonProcessingException, EntityNotFoundException;

    EventFullDto updateByUser(Long userId, Long eventId, EventUpdateDto eventUpdateDto, HttpServletRequest request) throws JsonProcessingException, EntityNotFoundException, ConflictException, InvalidParameterException;

    List<EventFullDto> searchEventsByAdmin(List<Long> usersId, List<String> states, List<Long> categories,
                                           LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer size, Integer from,
                                           HttpServletRequest request) throws JsonProcessingException;


    EventFullDto updateByAdmin(Long eventId, EventUpdateDto eventUpdateDto, HttpServletRequest request) throws JsonProcessingException, ConflictException, EntityNotFoundException, InvalidParameterException;
}