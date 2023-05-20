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
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @GetMapping
    public List<EventFullDto> searchEventsPublic(
            @RequestParam(defaultValue = "") String text,
            @RequestParam(defaultValue = "false") String paid,
            @RequestParam(defaultValue = "empty") String rangeStart,
            @RequestParam(defaultValue = "empty") String rangeEnd,
            @RequestParam(defaultValue = "false") String onlyAvailable,
            @RequestParam(defaultValue = "0") List<Integer> categories,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "10") @Positive Integer size,
            @RequestParam(defaultValue = "0") @Positive Integer from,
            HttpServletRequest request) {
        log.info("Received a request to search Events by text = {} paid = {} ", text, paid);
        log.info("Received a request to search Events by rangeStart = {} rangeEnd = {} ", rangeStart, rangeEnd);
        log.info("Received a request to search Events onlyAvailable = {} categories = {} sort = {}", onlyAvailable, categories, sort);
        log.info("Received a request to search Events by ... size {} from {} ", size, from);
        String endpointPath = request.getRequestURI();
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        LocalDateTime startTime;
        LocalDateTime endTime;
        if (rangeStart.equals("empty") || rangeEnd.equals("empty")) {
            startTime = LocalDateTime.now().minusYears(10);
            endTime = LocalDateTime.now().plusYears(10);
        } else {
            //startTime = LocalDateTime.parse(rangeStart.replaceAll(" ", "T"), formatter);
            //endTime = LocalDateTime.parse(rangeEnd.replaceAll(" ", "T"), formatter);
            startTime = LocalDateTime.parse(rangeStart, dateTimeFormatter);
            endTime = LocalDateTime.parse(rangeEnd, dateTimeFormatter);
        }
        return eventService.searchEventsPublic(text, Boolean.parseBoolean(paid), startTime, endTime,
                Boolean.getBoolean(onlyAvailable), categories, sort, size, from, endpointPath, request);
    }

    @GetMapping("/{id}")
    public EventFullDto getByEventId(@PathVariable Long id,
                                     HttpServletRequest request) {
        log.info("Received a request to get Event with id {} ", id);
        String endpointPath = request.getRequestURI();
        return eventService.getByEventId(id, endpointPath, request);
    }
}