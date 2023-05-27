package ru.practicum.request.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.EntityNotFoundException;
import ru.practicum.exception.InvalidParameterException;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.dto.RequestUpdateDto;
import ru.practicum.request.dto.RequestUpdateResultDto;
import ru.practicum.request.dto.RequestUpdateState;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.RequestState;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@Getter
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;

    private final EventRepository eventRepository;

    private final UserRepository userRepository;



    @Override
    public Request createRequest(Long userId, Long eventId) throws ConflictException, EntityNotFoundException {
        log.info("Call#RequestServiceImpl#createRequest userId: {], eventId: {}" , userId, eventId);
        Optional<User> user = userRepository.findById(userId) ;
        if (user.isEmpty()) {
            throw new EntityNotFoundException("Не найден пользователь с Id: " + userId);
        }
        Optional<Event> event = eventRepository.findById(eventId);
        if (event.isEmpty()) {
            throw new EntityNotFoundException("Не найдено событие с id: {}" + eventId);
        }
        Optional<Request> request = requestRepository.findByEventIdAndRequesterId(eventId, userId);
        if (request.isPresent()) {
            throw new ConflictException("Вы уже делали запрос на это событие");
        }

        if (event.get().getState() != EventState.PUBLISHED) {
            throw new ConflictException("Событие на опубликовано. Запрос отклонен");
        }
        if (Objects.equals(event.get().getInitiator().getId(), userId)) {
            throw new ConflictException("Вы не можете делать запрос на собственное событие");
        }

        int confirmedRequests = requestRepository.findByEventIdConfirmed(eventId);

        if (event.get().getParticipantLimit() != 0 && event.get().getParticipantLimit() <= confirmedRequests) {
            throw new ConflictException("Достигнут лимит участников");
        }
        RequestState status = RequestState.PENDING;
        if (!event.get().isRequestModeration() || event.get().getParticipantLimit() == 0) {
            status = RequestState.CONFIRMED;
        }
        Request newRequest = new Request(null,
                LocalDateTime.now(),
                event.get().getId(),
                user.get().getId(),
                status);
        return requestRepository.save(newRequest);
    }

    @Override
    public List<Request> getRequest(Long userId) {
        log.info("Call#RequestServiceImpl#getRequest userId: {}" , userId);
        return requestRepository.getAllByUserId(userId - 1);
    }

    @Override
    public List<Request> getByUserAndEventId(Long userId, Long eventId) {
        log.info("Call#RequestServiceImpl#getByUserAndEventId userId: {], eventId: {}" , userId, eventId);
        return requestRepository.getByUserAndEventId(userId, eventId);
    }

    @Override
    public Request cancelRequestByUser(Long userId, Long requestId) {
        log.info("Call#RequestServiceImpl#cancelRequestByUser userId: {], requestId: {}" , userId, requestId);
        Request request = requestRepository.getReferenceById(requestId);
            request.setStatus(RequestState.CANCELED);
        return requestRepository.save(request);
    }

    @Override
    public RequestUpdateResultDto updateRequestsStatus(Long userId, Long eventId, RequestUpdateDto requestUpdateDto) throws ConflictException, InvalidParameterException {
        log.info("Call#RequestServiceImpl#updateRequestsStatus userId: {}, eventId: {}, RequestUpdateDto: {}" , userId, eventId, requestUpdateDto);
        boolean isParticipationLimitGot = eventRepository.getReferenceById(eventId).getParticipantLimit()
                <= eventRepository.getReferenceById(eventId).getConfirmedRequests();
        List<Request> requests = requestRepository.getByRequestsList(requestUpdateDto.getRequestIds());
        RequestUpdateResultDto requestResultList = new RequestUpdateResultDto(new ArrayList<RequestDto>(), new ArrayList<RequestDto>());

        for (Request request : requests) {
            if (requestUpdateDto.getRequestIds().contains(request.getId())) {
                if (requestUpdateDto.getStatus().equals(RequestUpdateState.CONFIRMED)) {
                    if (isParticipationLimitGot) {
                        throw new InvalidParameterException("Достигнут лимит участников");
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