package ru.practicum.request.service;

import ru.practicum.request.dto.RequestUpdateDto;
import ru.practicum.request.dto.RequestUpdateResultDto;
import ru.practicum.request.model.Request;

import java.util.List;

public interface RequestService {
    Request create(Long userId, Long eventId);

    List<Request> get(Long id);

    List<Request> getByUserAndEventId(Long userId, Long eventId);

    Request cancelRequestByUser(Long userId, Long requestId);

    RequestUpdateResultDto updateRequestsStatus(Long userId, Long eventId, RequestUpdateDto requestUpdateDto);
}