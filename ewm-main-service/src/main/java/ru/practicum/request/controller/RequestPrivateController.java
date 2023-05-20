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
    public Request addRequest(@PathVariable @Positive Long userId, @RequestParam @Positive Long eventId) throws ConflictException {
        log.info("Call #RequestPrivateController#addRequest# userId {}, eventId: {}",userId, eventId);
        return requestService.create(userId, eventId);
    }

    @GetMapping
    public List<Request> getRequests(@PathVariable Long userId) {
        log.info("Call #RequestPrivateController#getRequest# userId {}",userId);
        return requestService.get(userId);
    }

    @PatchMapping("/{requestId}/cancel")
    public Request cancelRequestByUser(@PathVariable Long userId, @PathVariable Long requestId) {
        log.info("Call #RequestPrivateController#cancelRequestByUser# userId {}, requestId: {}",userId, requestId);
        return requestService.cancelRequestByUser(userId, requestId);
    }
}