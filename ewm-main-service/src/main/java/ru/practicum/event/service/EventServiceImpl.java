package ru.practicum.event.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventMapper;
import ru.practicum.event.dto.EventNewDto;
import ru.practicum.event.dto.EventUpdateDto;
import ru.practicum.event.location.LocationDto;
import ru.practicum.event.location.LocationMapper;
import ru.practicum.event.location.LocationRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.event.model.StateAction;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.statistic.HitDto;
import ru.practicum.statistic.HitMapper;
import ru.practicum.statistic.StatService;
import ru.practicum.user.dto.UserShortDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.repository.UserRepository;


import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Getter
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private static final String RANGE_START = "2000-01-01 00:01:01";

    private static final String RANGE_END = "2099-01-01 23:59:59";

    private final EventRepository eventRepository;

    private final LocationRepository locationRepository;

    private final CategoryRepository categoryRepository;

    private final UserRepository userRepository;

    private final RequestRepository requestRepository;

    private final StatsClient statsClient;

    private final StatService statService;

    @Override
    public EventFullDto create(Long userId, EventNewDto eventNewDto) {
        if (LocalDateTime.parse(eventNewDto.getEventDate().replaceAll(" ", "T")).isAfter(LocalDateTime.now().plusHours(2))) {
            Long locationId = locationRepository.save(eventNewDto.getLocation()).getId();
            LocationDto locationDto = LocationMapper.toLocationDtoFromLocation(eventNewDto.getLocation());
            Event event = eventRepository.save(EventMapper.toEventFromEventNewDto(userId, locationId, eventNewDto));
            Category category = categoryRepository.getReferenceById(event.getCategoryId());
            UserShortDto userShortDto = UserMapper.toUserShortDtoFromUser(userRepository.getReferenceById(userId));
            return getEventWithoutViews(event, locationDto, category, userShortDto);
        } else {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Событие не удовлетворяет правилам создания");
        }
    }

    @Override
    public List<EventFullDto> getByUserId(Long id, Integer size, Integer from, HttpServletRequest request) {
        List<Event> events = eventRepository.getByUserIdWithPagination(id, size, from);
        List<EventFullDto> eventFullDtoList = new ArrayList<>();
        for (Event event : events) {
            eventFullDtoList.add(toEventFullDtoFromEvent(event, false, request));
        }
        return eventFullDtoList;
    }

    @Override
    public EventFullDto getByUserAndEventId(Long userId, Long eventId, Integer size, Integer from, HttpServletRequest request) {
        Event event = eventRepository.getByUserAndEventId(userId, eventId, size, from);
        return toEventFullDtoFromEvent(event, false, request);
    }



    @Override
    public List<EventFullDto> searchEventsPublic(String text, boolean paid, LocalDateTime startTime, LocalDateTime endTime,
                                                 boolean onlyAvailable, List<Integer> categories, String sort,
                                                 Integer size, Integer from, String endpointPath, HttpServletRequest request)  {
        List<Event> events;
        List<EventFullDto> eventFullDtoList = new ArrayList<>();
        if (onlyAvailable) {
            if (categories.get(0) == 0) {
                events = eventRepository.searchEventsPublicOnlyAvailableAllCategories(
                        text, paid, startTime, endTime, sort, size, from);
            } else {
                events = eventRepository.searchEventsPublicOnlyAvailable(
                        text, paid, startTime, endTime, categories, sort, size, from);
            }
            for (Event event : events) {
                eventFullDtoList.add(toEventFullDtoFromEvent(event, false, request));
            }
        } else {
            if (categories.get(0) == 0) {
                events = eventRepository.searchEventsPublicAllCategories(
                        text, paid, startTime, endTime, sort.toLowerCase(), size, from);
            } else {
                events = eventRepository.searchEventsPublic(
                        text, paid, startTime, endTime, categories, sort.toLowerCase(), size, from);
            }
            for (Event event : events) {
                eventFullDtoList.add(toEventFullDtoFromEvent(event, false, request));
            }
        }
        return eventFullDtoList;
    }

    @Override
    public EventFullDto getByEventId(Long id, String endpointPath, HttpServletRequest request) {
        try {
            eventRepository.getByIdIfPublished(id).getTitle();
            Event event = eventRepository.getByIdIfPublished(id);
            //hitsClient.createHit("ewm-main-service", endpointPath);
            return toEventFullDtoFromEvent(event, false, request);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Event с запрошенным id не существует");
        }
    }

    @Override
    public EventFullDto updateByUser(Long userId, Long eventId, EventUpdateDto eventUpdateDto, HttpServletRequest request) {
        Event event = eventRepository.getReferenceById(eventId);
        LocalDateTime startTime;
        if (Optional.ofNullable(eventUpdateDto.getEventDate()).isEmpty()) {
            startTime = LocalDateTime.now().plusHours(10);
        } else {
            String rangeStart = eventUpdateDto.getEventDate();
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            startTime = LocalDateTime.parse(rangeStart.replaceAll(" ", "T"), formatter);
        }
        if (startTime.isAfter(LocalDateTime.now().plusHours(2)) && !event.getState().equals(EventState.PUBLISHED)
                && event.getInitiatorId().equals(userId)) {
            checkAndUpdateEvent(eventUpdateDto, event);
            if (Optional.ofNullable(eventUpdateDto.getStateAction()).isPresent()) {
                if (eventUpdateDto.getStateAction().equals(StateAction.SEND_TO_REVIEW)) {
                    event.setState(EventState.PENDING);
                }
                if (eventUpdateDto.getStateAction().equals(StateAction.CANCEL_REVIEW)) {
                    event.setState(EventState.CANCELED);
                }
            }
            return toEventFullDtoFromEvent(eventRepository.save(event), false, request);
        } else {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Событие должно быть в состоянии ожидания");
        }
    }

    private void checkAndUpdateEvent(EventUpdateDto eventUpdateDto, Event event) {
        if (Optional.ofNullable(eventUpdateDto.getTitle()).isPresent()) {
            event.setTitle(eventUpdateDto.getTitle());
        }
        if (Optional.ofNullable(eventUpdateDto.getDescription()).isPresent()) {
            event.setDescription(eventUpdateDto.getDescription());
        }
        if (Optional.ofNullable(eventUpdateDto.getAnnotation()).isPresent()) {
            event.setAnnotation(eventUpdateDto.getAnnotation());
        }
        if (Optional.ofNullable(eventUpdateDto.getCategory()).isPresent()) {
            event.setCategoryId(eventUpdateDto.getCategory());
        }
        if (Optional.ofNullable(eventUpdateDto.getEventDate()).isPresent()) {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            LocalDateTime eventDate = LocalDateTime.parse(eventUpdateDto.getEventDate().replaceAll(" ", "T"), formatter);
            event.setEventDate(eventDate);
        }
        if (Optional.ofNullable(eventUpdateDto.getLocation()).isPresent()) {
            event.setLocationId(locationRepository.save(eventUpdateDto.getLocation()).getId());
        }
        if (Optional.ofNullable(eventUpdateDto.getPaid()).isPresent()) {
            event.setPaid(Boolean.parseBoolean(eventUpdateDto.getPaid()));
        }
        if (Optional.ofNullable(eventUpdateDto.getParticipantLimit()).isPresent()) {
            event.setParticipantLimit(eventUpdateDto.getParticipantLimit());
        }
        if (Optional.ofNullable(eventUpdateDto.getRequestModeration()).isPresent()) {
            event.setRequestModeration(Boolean.parseBoolean(eventUpdateDto.getRequestModeration()));
        }
    }

    @Override
    public List<EventFullDto> searchEventsByAdmin(List<Long> usersId, List<String> states, List<Integer> categories,
                                                  LocalDateTime startTime, LocalDateTime endTime,
                                                  Integer size, Integer from, HttpServletRequest request) {
        List<Event> events;
        List<EventFullDto> eventFullDtoList = new ArrayList<>();
        if (usersId.get(0) == 0) {
            if (categories.get(0) == 0) {
                events = eventRepository.searchEventsByAdminFromAllUsersAndCategories(
                        states, startTime, endTime, size, from);
            } else {
                events = eventRepository.searchEventsByAdminFromAllUsers(
                        states, categories, startTime, endTime, size, from);
            }
        } else {
            if (categories.get(0) == 0) {
                events = eventRepository.searchEventsByAdminFromAllCategories(
                        usersId, states, startTime, endTime, size, from);
            } else {
                events = eventRepository.searchEventsByAdmin(
                        usersId, states, categories, startTime, endTime, size, from);
            }
        }
        for (Event event : events) {
            eventFullDtoList.add(toEventFullDtoFromEvent(event, false, request));
        }
        return eventFullDtoList;
    }

    @Override
    public EventFullDto updateByAdmin(Long eventId, EventUpdateDto eventUpdateDto, HttpServletRequest request) throws ConflictException {
        Event event = eventRepository.getReferenceById(eventId);
        LocalDateTime startTime;
        if (Optional.ofNullable(eventUpdateDto.getEventDate()).isEmpty()) {
            startTime = LocalDateTime.now().plusHours(10);
        } else {
            String rangeStart = eventUpdateDto.getEventDate();
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            startTime = LocalDateTime.parse(rangeStart.replaceAll(" ", "T"), formatter);
        }
        if (startTime.isAfter(LocalDateTime.now().plusHours(1)) && event.getState().equals(EventState.PENDING)) {
            checkAndUpdateEvent(eventUpdateDto, event);
            if (Optional.ofNullable(eventUpdateDto.getStateAction()).isPresent()) {
                if (eventUpdateDto.getStateAction().equals(StateAction.PUBLISH_EVENT)) {
                    event.setState(EventState.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                }
                if (eventUpdateDto.getStateAction().equals(StateAction.REJECT_EVENT)) {
                    event.setState(EventState.CANCELED);
                }
            }
            return toEventFullDtoFromEvent(eventRepository.save(event), true, request);
        } else {
            throw new ConflictException("Проверьте статус и время начала события.");
//            throw new ResponseStatusException(HttpStatus.CONFLICT, "Событие должно быть в состоянии ожидания " +
//                    "и дата начала изменяемого события должна быть не ранее чем за час от даты публикации ");
        }
    }

    private EventFullDto toEventFullDtoFromEvent(Event event, boolean updating, HttpServletRequest request) {
        LocationDto locationDto = LocationMapper.toLocationDtoFromLocation(locationRepository.getReferenceById(event.getLocationId()));
        Category category = categoryRepository.getReferenceById(event.getCategoryId());
        UserShortDto userShortDto = UserMapper.toUserShortDtoFromUser(userRepository.getReferenceById(event.getInitiatorId()));
        if (updating) {
            return getEventWithoutViews(event, locationDto, category, userShortDto);
        } else {
            return getEventWithViews(event, locationDto, category, userShortDto, request);
        }
    }

    private EventFullDto getEventWithViews(Event event, LocationDto locationDto, Category category, UserShortDto userShortDto, HttpServletRequest request) {
        statService.addEventStat(HitMapper.toEndpointHit("ewm-main-service", request));
        String uriEvent = "/events/" + event.getId().toString();
        //List<EndpointHitDto> hitDtos = (List<EndpointHitDto>) statService.getStatistics(RANGE_START, RANGE_END, List.of(uriEvent), false );
        List<ViewStatsDto> hitDtos = statService.getStatistics(RANGE_START, RANGE_END, List.of(uriEvent), false );
        Integer views = 0;
        if (!hitDtos.isEmpty()) {
            views = hitDtos.size();
        }
        Integer confirmedRequests = requestRepository.getAllByEventIdAndConfirmedStatus(event.getId());
        return EventMapper.toEventFullDtoFromEvent(event, category, locationDto, userShortDto, views, confirmedRequests);
    }

    private EventFullDto getEventWithoutViews(Event event, LocationDto locationDto, Category category, UserShortDto userShortDto) {
        Integer views = 0;
        Integer confirmedRequests = requestRepository.getAllByEventIdAndConfirmedStatus(event.getId());
        return EventMapper.toEventFullDtoFromEvent(event, category, locationDto, userShortDto, views, confirmedRequests);
    }
}