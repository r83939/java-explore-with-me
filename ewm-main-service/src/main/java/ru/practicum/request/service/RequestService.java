package ru.practicum.request.service;

import ru.practicum.exception.ConflictException;
import ru.practicum.exception.EntityNotFoundException;
import ru.practicum.exception.InvalidParameterException;
import ru.practicum.request.dto.RequestUpdateDto;
import ru.practicum.request.dto.RequestUpdateResultDto;
import ru.practicum.request.model.Request;

import java.util.List;

public interface RequestService {
    Request createRequest(Long userId, Long eventId) throws ConflictException, EntityNotFoundException;

    List<Request> getRequest(Long id);

    List<Request> getByUserAndEventId(Long userId, Long eventId);

    Request cancelRequestByUser(Long userId, Long requestId);

    RequestUpdateResultDto updateRequestsStatus(Long userId, Long eventId, RequestUpdateDto requestUpdateDto) throws ConflictException, InvalidParameterException, EntityNotFoundException;
}