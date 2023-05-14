package ru.practicum.request.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.EntityNotFoundException;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.service.RequestsService;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

@Validated
@RestController
@RequestMapping("/users/{userId}/requests")
public class RequestsController {
    private final RequestsService requestsService;

    @Autowired
    public RequestsController(RequestsService requestsService) {
        this.requestsService = requestsService;
    }

    @GetMapping
    public List<RequestDto> findParticipationRequests(@Positive @PathVariable Long userId) {
        return requestsService.findByUserId(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RequestDto addRequest(@Positive @PathVariable Long userId,
                                    @Positive @RequestParam Long eventId) throws ConflictException, EntityNotFoundException {
        return requestsService.addRequest(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public RequestDto revokeRequest(@PathVariable @Positive @NotNull Long userId,
                                    @PathVariable @Positive @NotNull Long requestId) {
        return requestsService.revokeRequest(userId, requestId);
    }

}
