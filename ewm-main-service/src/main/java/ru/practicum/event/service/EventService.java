package ru.practicum.event.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventNewDto;
import ru.practicum.event.dto.EventUpdateDto;
import ru.practicum.exception.ConflictException;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    EventFullDto create(Long id, EventNewDto eventNewDto) throws ConflictException;

    List<EventFullDto> searchEventsPublic(String text, boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                          boolean onlyAvailable, List<Integer> categories, String sort,
                                          Integer from, Integer size, String endpointPath, HttpServletRequest request) throws JsonProcessingException;

    EventFullDto getByEventId(Long id, String endpointPath, HttpServletRequest request);

    List<EventFullDto> getByUserId(Long id, Integer size, Integer from, HttpServletRequest request) throws JsonProcessingException;

    EventFullDto getByUserAndEventId(Long userId, Long eventId, Integer size, Integer from, HttpServletRequest request) throws JsonProcessingException;

    EventFullDto updateByUser(Long userId, Long eventId, EventUpdateDto eventUpdateDto, HttpServletRequest request) throws JsonProcessingException;

    List<EventFullDto> searchEventsByAdmin(List<Long> usersId, List<String> states, List<Integer> categories,
                                           LocalDateTime startTime, LocalDateTime endTime,
                                           Integer from, Integer size, HttpServletRequest request) throws JsonProcessingException;

    EventFullDto updateByAdmin(Long eventId, EventUpdateDto eventUpdateDto, HttpServletRequest request) throws JsonProcessingException, ConflictException;
}