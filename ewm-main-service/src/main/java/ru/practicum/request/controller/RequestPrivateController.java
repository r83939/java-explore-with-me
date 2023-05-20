package ru.practicum.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.exception.ConflictException;
import ru.practicum.request.model.Request;
import ru.practicum.request.service.RequestServiceImpl;

import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}/requests")
public class RequestPrivateController {

    private final RequestServiceImpl requestService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Request create(@PathVariable @Positive Long userId, @RequestParam @Positive Long eventId) throws ConflictException {
        log.info("Received a request from User with id {} to participate in Event with id {}", userId, eventId);
        return requestService.create(userId, eventId);
    }

    @GetMapping
    public List<Request> get(@PathVariable Long userId) {
        log.info("Received a request to get Requests of User with id {} ", userId);
        return requestService.get(userId);
    }

    @PatchMapping("/{requestId}/cancel")
    public Request cancelRequestByUser(@PathVariable Long userId, @PathVariable Long requestId) {
        log.info("Received a request to cancel a request with id: {}, user: {}", userId, requestId);
        return requestService.cancelRequestByUser(userId, requestId);
    }
}