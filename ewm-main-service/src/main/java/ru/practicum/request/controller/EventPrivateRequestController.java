package ru.practicum.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.dto.RequestUpdateDto;
import ru.practicum.request.dto.RequestUpdateResultDto;
import ru.practicum.request.model.Request;
import ru.practicum.request.service.RequestServiceImpl;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}/events/{eventId}/requests")
public class EventPrivateRequestController {

    private final RequestServiceImpl requestService;

    @PatchMapping
    public RequestUpdateResultDto updateRequestsStatus(@PathVariable Long userId,
                                                       @PathVariable Long eventId,
                                                       @RequestBody @Valid RequestUpdateDto requestUpdateDto) {
        log.info("Received a request from user with id {} to update event id: {}, requestUpdateDto: {}", userId, eventId, requestUpdateDto);
        return requestService.updateRequestsStatus(userId, eventId, requestUpdateDto);
    }

    @GetMapping
    public List<Request> getByUserAndEventId(@PathVariable Long userId,
                                             @PathVariable Long eventId) {
        log.info("Received a request to get Requests of User with id {} in Event id {}", userId, eventId);
        return requestService.getByUserAndEventId(userId, eventId);
    }
}