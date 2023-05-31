package ru.practicum.service;

import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;
import ru.practicum.exception.InvalidParameterException;

import java.time.LocalDateTime;
import java.util.List;

public interface StatService {
    List<ViewStatsDto> getStatistics(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) throws InvalidParameterException;

    EndpointHitDto addEvent(EndpointHitDto endpointHitDto);
}
