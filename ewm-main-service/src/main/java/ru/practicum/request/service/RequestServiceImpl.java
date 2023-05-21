package ru.practicum.request.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.dto.RequestUpdateDto;
import ru.practicum.request.dto.RequestUpdateResultDto;
import ru.practicum.request.dto.RequestUpdateState;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.RequestState;
import ru.practicum.request.repository.RequestRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Getter
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;

    private final EventRepository eventRepository;

    @Override
    public Request create(Long userId, Long eventId) throws ConflictException {
        boolean isExists = !getByUserAndEventId(userId - 1, eventId).isEmpty();
        boolean isYourEvent = eventRepository.getReferenceById(eventId).getInitiator().getId().equals(userId);
        boolean isEventPublished = eventRepository.getReferenceById(eventId).getState().equals(EventState.PUBLISHED);
        boolean isParticipationLimitGot = eventRepository.getReferenceById(eventId).getParticipantLimit()
                <= eventRepository.getReferenceById(eventId).getConfirmedRequests();
        boolean isModerateOn = eventRepository.getReferenceById(eventId).isRequestModeration();

        if (!isExists && !isYourEvent && isEventPublished) {
            Request request;
            if (!isModerateOn && !isParticipationLimitGot) {
                request = new Request(
                        null,
                        LocalDateTime.now(),
                        eventId,
                        userId - 1,
                        RequestState.valueOf("CONFIRMED")
                );
                Event event = eventRepository.getReferenceById(eventId);
                event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            } else if (isParticipationLimitGot) {
                throw new ConflictException("Нет события с id: " + eventId);
            } else {
                request = new Request(
                        null,
                        LocalDateTime.now(),
                        eventId,
                        userId - 1,
                        RequestState.valueOf("PENDING")
                );
            }
            return requestRepository.save(request);
        } else {
            System.out.println("CONFLICT");
            throw new ConflictException("Нет события с id: " + eventId);
        }
    }

    @Override
    public List<Request> get(Long userId) {
        return requestRepository.getAllByUserId(userId - 1);
    }

    @Override
    public List<Request> getByUserAndEventId(Long userId, Long eventId) {
        return requestRepository.getByUserAndEventId(userId, eventId);
    }

    @Override
    public Request cancelRequestByUser(Long userId, Long requestId) {
        Request request = requestRepository.getReferenceById(requestId);
            request.setStatus(RequestState.CANCELED);
        return requestRepository.save(request);
    }

    @Override
    public RequestUpdateResultDto updateRequestsStatus(Long userId, Long eventId, RequestUpdateDto requestUpdateDto) throws ConflictException {
        boolean isParticipationLimitGot = eventRepository.getReferenceById(eventId).getParticipantLimit()
                <= eventRepository.getReferenceById(eventId).getConfirmedRequests();
        List<Request> requests = requestRepository.getByRequestsList(requestUpdateDto.getRequestIds());
        RequestUpdateResultDto requestResultList = new RequestUpdateResultDto(new ArrayList<RequestDto>(), new ArrayList<RequestDto>());

        for (Request request : requests) {
            if (requestUpdateDto.getRequestIds().contains(request.getId())) {
                if (requestUpdateDto.getStatus().equals(RequestUpdateState.CONFIRMED)) {
                    if (isParticipationLimitGot) {
                        throw new ConflictException("Достигнут лимит участников");
                    } else {
                        request.setStatus(RequestState.CONFIRMED);
                        Event event = eventRepository.getReferenceById(eventId);
                        event.setConfirmedRequests(event.getConfirmedRequests() + 1);
                        requestRepository.save(request);
                        requestResultList.getConfirmedRequests().add(RequestMapper.toRequestDtoFromRequest(request));
                    }
                } else if (requestUpdateDto.getStatus().equals(RequestUpdateState.REJECTED)
                        && !request.getStatus().equals(RequestState.CONFIRMED)) {
                    request.setStatus(RequestState.REJECTED);
                    requestRepository.save(request);
                    requestResultList.getRejectedRequests().add(RequestMapper.toRequestDtoFromRequest(request));
                } else {
                    throw new ConflictException("Нельзя отменить уже принятую заявку");
                }
            }
        }
        return requestResultList;
    }
}