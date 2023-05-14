package ru.practicum.request.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.dto.EventRequestStatusUpdateRequest;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.EntityNotFoundException;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.dto.RequestUpdateDto;
import ru.practicum.request.model.EventState;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.RequestStatus;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.request.mapper.RequestsMapper.requestMapper;

@Slf4j
@Service
public class RequestsService {
    private final EventRepository eventRepo;
    private final UserRepository userRepo;
    private final RequestRepository requestRepo;

    public RequestsService(EventRepository eventRepo, UserRepository userRepo, RequestRepository requestRepo) throws ConflictException, EntityNotFoundException {
        this.eventRepo = eventRepo;
        this.userRepo= userRepo;
        this.requestRepo= requestRepo;
    }

    public List<RequestDto> findByUserId(Long userId) {
        log.info("Request sent");
        return requestRepo.findByUserId(userId).stream()
                .map(requestMapper::toRequestDto)
                .collect(Collectors.toList());
    }

    public List<RequestDto> findByEventIdAndInitiatorId(Long eventId, Long userId) {
        log.info("Requests sent");
        return requestRepo.findByEventIdAndInitiatorId(eventId, userId).stream()
                .map(requestMapper::toRequestDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public RequestDto addRequest(Long userId, Long eventId) throws ConflictException, EntityNotFoundException {
        Optional<User> user = userRepo.findById(userId);
        if (user.isEmpty()) {
            throw new EntityNotFoundException("User not found");
        }

        Optional<Event> event = eventRepo.findById(eventId);
        if (event.isEmpty()) {
            throw new EntityNotFoundException("Event not found");
        }

        if (event.get().getEventState() != EventState.PUBLISHED) {
            throw new ConflictException("Event is not published. Request rejected");
        }
        if (Objects.equals(event.get().getInitiator().getId(), userId)) {
            throw new ConflictException("You can't send request to your own event");
        }
        int confirmedRequests = requestRepo.findByEventIdConfirmed(eventId).size();
        if (event.get().getParticipantLimit() != 0 && event.get().getParticipantLimit() <= confirmedRequests) {
            throw new ConflictException("Participant limit reached");
        }
        RequestStatus status = RequestStatus.PENDING;
        if (!event.get().getRequestModeration() || event.get().getParticipantLimit() == 0) {
            status = RequestStatus.CONFIRMED;
        }
        Request request = new Request(null,
                LocalDateTime.now(),
                event.get(),
                user.get(),
                status);
        Optional<Request> check = requestRepo
                .findByEventIdAndRequesterId(eventId, userId);
        if (check.isPresent()) throw new ConflictException("You already have request to event");
        request = requestRepo.save(request);
        log.info("Request created");
        return requestMapper.toRequestDto(request);
    }

    @Transactional
    public RequestDto revokeRequest(Long userId, Long requestId) {
        Optional<Request> request = requestRepo.findByIdAndRequesterId(requestId, userId);
        if (request.isEmpty()) {

        }
        request.get().setStatus(RequestStatus.CANCELED);
        log.info("Request canceled");
        return requestMapper.toRequestDto(requestRepo.save(request.get()));
    }

    @Transactional
    public RequestUpdateDto requestProcessing(Long userId, Long eventId,
                                              EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) throws EntityNotFoundException, ConflictException {
        Optional<Event> event = eventRepo.findById(eventId);

        if (event.isEmpty()) {
            throw new EntityNotFoundException("Event not found");
        }

        if (!event.get().getInitiator().getId().equals(userId)) {
            throw new EntityNotFoundException("You don't have event with id " + eventId);
        }
        if (!event.get().getRequestModeration() || event.get().getParticipantLimit() == 0) {
            throw new ConflictException("Confirmation is not required");
        }
        RequestUpdateDto requestUpdateDto = new RequestUpdateDto(new ArrayList<>(), new ArrayList<>());
        Integer confirmedRequests = requestRepo.findByEventIdConfirmed(eventId).size();
        List<Request> requests = requestRepo.findByEventIdAndRequestsIds(eventId,
                eventRequestStatusUpdateRequest.getRequestIds());
        if (Objects.equals(eventRequestStatusUpdateRequest.getStatus(), RequestStatus.CONFIRMED.name())
                && confirmedRequests + requests.size() > event.get().getParticipantLimit()) {
            requests.forEach(request -> request.setStatus(RequestStatus.REJECTED));
            List<RequestDto> requestDto = requests.stream()
                    .map(requestMapper::toRequestDto)
                    .collect(Collectors.toList());
            requestUpdateDto.setRejectedRequests(requestDto);
            requestRepo.saveAll(requests);
            throw new ConflictException("Requests limit exceeded");
        }
        if (eventRequestStatusUpdateRequest.getStatus().equalsIgnoreCase(RequestStatus.REJECTED.name())) {
            for (Request request : requests) {
                if (request.getStatus().equals(RequestStatus.CONFIRMED)) {
                    throw new ConflictException("You can't reject confirmed request");
                }
                request.setStatus(RequestStatus.REJECTED);
            }
            List<RequestDto> requestDto = requests.stream()
                    .map(requestMapper::toRequestDto)
                    .collect(Collectors.toList());
            requestUpdateDto.setRejectedRequests(requestDto);
            requestRepo.saveAll(requests);
        } else if (eventRequestStatusUpdateRequest.getStatus().equalsIgnoreCase(RequestStatus.CONFIRMED.name())
                && eventRequestStatusUpdateRequest.getRequestIds().size() <= event.get().getParticipantLimit() - confirmedRequests) {
            requests.forEach(request -> request.setStatus(RequestStatus.CONFIRMED));
            List<RequestDto> requestDto = requests.stream()
                    .map(requestMapper::toRequestDto)
                    .collect(Collectors.toList());
            requestUpdateDto.setConfirmedRequests(requestDto);
            requestRepo.saveAll(requests);
        }
        return requestUpdateDto;
    }

}
