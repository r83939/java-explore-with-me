package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventUpdateDto;
import ru.practicum.event.service.EventServiceImpl;
import ru.practicum.exception.ConflictException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/events")
public class EventAdminController {

    private final EventServiceImpl eventService;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @GetMapping
    public List<EventFullDto> searchEventsByAdmin(
            @RequestParam(defaultValue = "0") List<Long> users,
            @RequestParam(defaultValue = "WAITING, PUBLISHED, CANCELED") List<String> states,
            @RequestParam(defaultValue = "0") List<Integer> categories,
            @RequestParam(defaultValue = "2000-01-01 00:01:01") String rangeStart,
            @RequestParam(defaultValue = "2099-01-01 23:59:59") String rangeEnd,
            @RequestParam(defaultValue = "10") @Positive Integer size,
            @RequestParam(defaultValue = "0") @Positive Integer from,
            HttpServletRequest request) {
        log.info("Received a request from Admin to get Events size {} from {} ", size, from);
        //DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        //LocalDateTime startTime = LocalDateTime.parse(rangeStart.replaceAll(" ", "T"), formatter);
        //LocalDateTime endTime = LocalDateTime.parse(rangeEnd.replaceAll(" ", "T"), formatter);
        LocalDateTime startTime = LocalDateTime.parse(rangeStart, dateTimeFormatter);
        LocalDateTime endTime = LocalDateTime.parse(rangeEnd, dateTimeFormatter);
        return eventService.searchEventsByAdmin(users, states, categories, startTime, endTime, size, from, request);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateByAdmin(@PathVariable Long eventId,
                                      @RequestBody EventUpdateDto eventUpdateDto,
                                      HttpServletRequest request) throws ConflictException {
        log.info("Received a request from Admin to update Event with id {} eventUpdateDto:{} ", eventId, eventUpdateDto);
        return eventService.updateByAdmin(eventId, eventUpdateDto, request);
    }
}