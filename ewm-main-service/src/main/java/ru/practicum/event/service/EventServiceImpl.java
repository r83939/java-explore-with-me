package ru.practicum.event.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import ru.practicum.ViewStatsDto;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.location.Location;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.dto.EventNewDto;
import ru.practicum.event.dto.EventUpdateDto;
import ru.practicum.event.location.LocationDto;
import ru.practicum.event.location.LocationMapper;
import ru.practicum.event.location.LocationRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.event.model.Sort;
import ru.practicum.event.model.StateAction;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.EntityNotFoundException;
import ru.practicum.exception.InvalidParameterException;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.statistic.HitMapper;
import ru.practicum.statistic.StatClient;
import ru.practicum.statistic.StatService;
import ru.practicum.user.dto.UserShortDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;


import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.util.stream.Collectors.toList;

@Service
@Getter
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private static final String RANGE_START = "2000-01-01 00:01:01";
    private static final String RANGE_END = "2099-01-01 23:59:59";
    private static final String APP_NAME = "ewm-main-service";
    private static final String URI = "/events/";
    private static final long HOURS_BEFORE_START = 2L;
    private static final long ADMIN_HOURS_BEFORE_START = 1L;
    private static final long MINUTE_LATER_NOW = 1L;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final EventRepository eventRepository;

    private final LocationRepository locationRepository;

    private final CategoryRepository categoryRepository;

    private final UserRepository userRepository;

    private final RequestRepository requestRepository;

    private final StatService statService;

    private final StatClient statClient;


    @Override
    public EventFullDto addEvent(Long userId, EventNewDto eventNewDto) throws EntityNotFoundException, InvalidParameterException {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new EntityNotFoundException("Нет пользователя с id: " + userId);
        }
        Optional<Category> category = categoryRepository.findById(eventNewDto.getCategory());
        if (category.isEmpty()) {
            throw new EntityNotFoundException("Нет категории с id: " + eventNewDto.getCategory());
        }
        if (LocalDateTime.parse(eventNewDto.getEventDate(), dateTimeFormatter).isAfter(LocalDateTime.now().plusHours(HOURS_BEFORE_START))) {
            Location location = locationRepository.save(eventNewDto.getLocation());
            LocationDto locationDto = LocationMapper.toLocationDtoFromLocation(eventNewDto.getLocation());
            Event event = eventRepository.save(EventMapper.toEventFromEventNewDto(user.get(), location, eventNewDto, category.get()));

            UserShortDto userShortDto = UserMapper.toUserShortDtoFromUser(userRepository.getReferenceById(userId));
            return getEventWithoutViews(event, locationDto, userShortDto);
        } else {
            throw new InvalidParameterException("Проверьте дату события.");
        }
    }

    @Override
    public List<EventFullDto> getByUserId(Long id, Integer size, Integer from, HttpServletRequest request) {
        List<Event> events = eventRepository.getByUserIdWithPagination(id, size, from);

        List<Long> eventIds;
        eventIds = events.stream()
                .map(Event::getId)
                .collect(toList());

        Map<Long, Integer> confirmedRequests = requestRepository.getConfirmedRequestsByEventIds(eventIds);

        List<String> uriEventList = events.stream()
                .map(e -> URI + e.getId().toString())
                .collect(toList());

        List<ViewStatsDto> viewStatsDtos = statClient.getStats(RANGE_START, RANGE_END, uriEventList, false);

        Map<Long, Long> eventViews = getEventsMap(viewStatsDtos);

        List<EventFullDto> eventFullDtoList = new ArrayList<>();
        for (Event event : events) {
            eventFullDtoList.add(new EventFullDto(
                    event.getId(),
                    event.getTitle(),
                    event.getDescription(),
                    event.getAnnotation(),
                    event.getState(),
                    event.getCategory(),
                    event.getCreatedOn(),
                    event.getEventDate().format(dateTimeFormatter),
                    event.getPublishedOn(),
                    confirmedRequests.get(event.getId()),
                    LocationMapper.toLocationDtoFromLocation(event.getLocation()),
                    UserMapper.toUserShortDtoFromUser(event.getInitiator()),
                    event.isPaid(),
                    event.getParticipantLimit(),
                    event.isRequestModeration(),
                    eventViews.get(event.getId()))
            );
        }
        return eventFullDtoList;

//        List<EventFullDto> eventFullDtoList = new ArrayList<>();
//        for (Event event : events) {
//            eventFullDtoList.add(toEventFullDtoFromEvent(event, false));
//        }
//        return eventFullDtoList;
    }

    @Override
    public EventFullDto getByUserAndEventId(Long userId, Long eventId, HttpServletRequest request) throws EntityNotFoundException {

        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new EntityNotFoundException("Нет пользователя с id: " + userId );
        }
        Optional<Event> event = eventRepository.findById(eventId);
        if (event.isEmpty()) {
            throw new EntityNotFoundException("Нет события с id: " + eventId );
        }
        if (event.get().getInitiator().getId() != userId) {
            return null;
        }

        //statService.addEventStat(HitMapper.toEndpointHit(APP_NAME, request));
        String eventUri = URI + event.get();
        List<ViewStatsDto> viewStatsDtos = statClient.getStats(RANGE_START, RANGE_END, List.of(eventUri), false);

        Integer confirmedRequest = requestRepository.getConfirmedRequestsByEventId(eventId);

        EventFullDto eventFullDto = new EventFullDto(
                event.get().getId(),
                event.get().getTitle(),
                event.get().getDescription(),
                event.get().getAnnotation(),
                event.get().getState(),
                event.get().getCategory(),
                event.get().getCreatedOn(),
                event.get().getEventDate().format(dateTimeFormatter),
                event.get().getPublishedOn(),
                confirmedRequest,
                LocationMapper.toLocationDtoFromLocation(event.get().getLocation()),
                UserMapper.toUserShortDtoFromUser(event.get().getInitiator()),
                event.get().isPaid(),
                event.get().getParticipantLimit(),
                event.get().isRequestModeration(),
                viewStatsDtos.get(0).getHits()
        );
        return  eventFullDto;

        //return toEventFullDtoFromEvent(event, false);
    }

    @Override
    public List<EventFullDto> searchEventsPublic(String text,
                                                  List<Long> categories,
                                                  Boolean paid,
                                                  String rangeStart,
                                                  String rangeEnd,
                                                  Boolean onlyAvailable,
                                                  Sort sort,
                                                  Integer from,
                                                  Integer size,
                                                  HttpServletRequest request) throws InvalidParameterException, IOException, ConflictException {
        PageRequest pageable = PageRequest.of(from / size, size);
        LocalDateTime startTime;
        LocalDateTime endTime;
        if (rangeStart != null && rangeEnd != null) {
            startTime = LocalDateTime.parse(rangeStart, dateTimeFormatter);
            endTime = LocalDateTime.parse(rangeEnd, dateTimeFormatter);
            if (startTime.isAfter(endTime)) {
                throw new InvalidParameterException("Время начала не должно быть позже времени окончания");
            }
        } else {
            startTime = LocalDateTime.now().plusMinutes(MINUTE_LATER_NOW);
            endTime = LocalDateTime.parse(RANGE_END, dateTimeFormatter);
        }
        if (rangeStart != null) {
            startTime = LocalDateTime.parse(rangeStart, dateTimeFormatter);
        }
        if (rangeEnd != null) {
            endTime = LocalDateTime.parse(rangeEnd, dateTimeFormatter);
        }
        List<Event> events = new ArrayList<>();
        if (sort == null || sort.equals(Sort.VIEWS)) {
            events = eventRepository.findEventsByParamsOrderById(text, categories, paid, startTime, endTime, onlyAvailable, pageable);
        }
        else if (sort != null && sort.equals(Sort.EVENT_DATE)) {
            events = eventRepository.findEventsByParamsOrderByDate(text, categories, paid, startTime, endTime, onlyAvailable, pageable);
        }
        else {
            throw new InvalidParameterException("Неверный параметр sort:" + sort);
        }

        List<Long> eventIds;
        eventIds = events.stream()
                .map(Event::getId)
                .collect(toList());

        Map<Long, Integer> confirmedRequests = requestRepository.getConfirmedRequestsByEventIds(eventIds);

        List<String> uriEventList = events.stream()
                .map(e -> URI + e.getId().toString())
                .collect(toList());

        List<ViewStatsDto> viewStatsDtos = statClient.getStats(RANGE_START, RANGE_END, uriEventList, false);

        Map<Long, Long> eventViews = getEventsMap(viewStatsDtos);

        List<EventFullDto> eventFullDtoList = new ArrayList<>();
        for (Event event : events) {
            eventFullDtoList.add(new EventFullDto(
                    event.getId(),
                    event.getTitle(),
                    event.getDescription(),
                    event.getAnnotation(),
                    event.getState(),
                    event.getCategory(),
                    event.getCreatedOn(),
                    event.getEventDate().format(dateTimeFormatter),
                    event.getPublishedOn(),
                    confirmedRequests.get(event.getId()),
                    LocationMapper.toLocationDtoFromLocation(event.getLocation()),
                    UserMapper.toUserShortDtoFromUser(event.getInitiator()),
                    event.isPaid(),
                    event.getParticipantLimit(),
                    event.isRequestModeration(),
                    eventViews.get(event.getId()))
            );
        }

        statService.addEventStat(HitMapper.toEndpointHit(APP_NAME, request));
        return eventFullDtoList;
    }

    private Map<Long, Long> getEventsMap(List<ViewStatsDto> hitDtos) {
        Map<Long, Long> map = new HashMap<>();
        for (ViewStatsDto viewStatsDto : hitDtos) {

            map.put( Long.parseLong(viewStatsDto.getUri().replace("/events/", "")), viewStatsDto.getHits());
        }
        return map;
    }

    @Override
    public List<EventFullDto> searchEventsPublic0(String text, boolean paid, String rangeStart, String rangeEnd,
                                                 boolean onlyAvailable, List<Integer> categories, String sort,
                                                 Integer size, Integer from, HttpServletRequest request) throws InvalidParameterException {

        LocalDateTime startTime;
        LocalDateTime endTime;
        if (rangeStart.equals("empty") || rangeEnd.equals("empty")) {
            startTime = LocalDateTime.now().plusMinutes(MINUTE_LATER_NOW);
            endTime = LocalDateTime.parse(RANGE_END, dateTimeFormatter);
        } else {
            startTime = LocalDateTime.parse(rangeStart, dateTimeFormatter);
            endTime = LocalDateTime.parse(rangeEnd, dateTimeFormatter);
            if (endTime.isBefore(startTime)) {
                throw new InvalidParameterException("Время окончания события должно быть позже времени начала.");
            }
        }

        List<Event> events;
        List<EventFullDto> eventFullDtoList = new ArrayList<>();
        if (sort.equals("EVENT_DATE")) {
            sort = "published_on";
        }
        if (onlyAvailable) {
            if (categories == null) {
                events = eventRepository.searchEventsPublicOnlyAvailableAllCategories(
                        text, paid, startTime, endTime, sort, size, from);
            } else {
                events = eventRepository.searchEventsPublicOnlyAvailable(
                        text, paid, startTime, endTime, categories, sort, size, from);
            }
            for (Event event : events) {
                eventFullDtoList.add(toEventFullDtoFromEvent(event, false));
            }
        } else {
            if (categories == null) {
                events = eventRepository.searchEventsPublicAllCategories(
                        text, paid, startTime, endTime, sort.toLowerCase(), size, from);
            } else {
                events = eventRepository.searchEventsPublic(
                        text, paid, startTime, endTime, categories, sort.toLowerCase(), size, from);
            }
            for (Event event : events) {
                eventFullDtoList.add(toEventFullDtoFromEvent(event, false));
            }
        }
        statService.addEventStat(HitMapper.toEndpointHit(APP_NAME, request));
        return eventFullDtoList;
    }

    @Override
    public EventFullDto getByEventId(Long id, HttpServletRequest request) throws EntityNotFoundException {
//        try {
//            Event event = eventRepository.getByIdIfPublished(id);
//            statService.addEventStat(HitMapper.toEndpointHit(APP_NAME, request));
//            return toEventFullDtoFromEvent(event, false);
//        } catch (Exception e) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Event с запрошенным id не существует");
//        }
        Optional<Event> event =eventRepository.findById(id);
        if (event.isEmpty()) {
            throw new EntityNotFoundException("Нет событиф с id: " + id );
        }
        statService.addEventStat(HitMapper.toEndpointHit(APP_NAME, request));
        String eventUri = URI + event.get();
        List<ViewStatsDto> viewStatsDtos = statClient.getStats(RANGE_START, RANGE_END, List.of(eventUri), false);

        Integer confirmedRequest = requestRepository.getConfirmedRequestsByEventId(id);

        EventFullDto eventFullDto = new EventFullDto(
                event.get().getId(),
                event.get().getTitle(),
                event.get().getDescription(),
                event.get().getAnnotation(),
                event.get().getState(),
                event.get().getCategory(),
                event.get().getCreatedOn(),
                event.get().getEventDate().format(dateTimeFormatter),
                event.get().getPublishedOn(),
                confirmedRequest,
                LocationMapper.toLocationDtoFromLocation(event.get().getLocation()),
                UserMapper.toUserShortDtoFromUser(event.get().getInitiator()),
                event.get().isPaid(),
                event.get().getParticipantLimit(),
                event.get().isRequestModeration(),
                viewStatsDtos.get(0).getHits()
        );
        return  eventFullDto;
    }

    @Override
    public EventFullDto updateByUser(Long userId, Long eventId, EventUpdateDto eventUpdateDto, HttpServletRequest request) throws EntityNotFoundException, InvalidParameterException, ConflictException {
        Optional<Event> event = eventRepository.findById(eventId);
        if (event.isEmpty()) {
            throw new EntityNotFoundException("Нет события с id: " + eventId);
        }
        if (event.get().getInitiator().getId() != userId) {
            throw new ConflictException("Пользователь не может изменять не свои события");
        }

        if (event.get().getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Нельзя изменить событие в статусе PUBLISHED");
        }
        LocalDateTime startTime;
        if (Optional.ofNullable(eventUpdateDto.getEventDate()).isEmpty()) {
            startTime = event.get().getEventDate();
        } else {
            startTime = LocalDateTime.parse(eventUpdateDto.getEventDate(), dateTimeFormatter);
        }
        //if (startTime.isAfter(LocalDateTime.now().plusHours(HOURS_BEFORE_START)) && !event.get().getState().equals(EventState.PUBLISHED)) {
        if (startTime.isBefore(LocalDateTime.now().plusHours(HOURS_BEFORE_START))) {
            throw new ConflictException("Дата и время на которые намечено событие не может быть раньше, чем через два часа от текущего момента");
        }

        checkAndUpdateEvent(eventUpdateDto, event.get());
        if (Optional.ofNullable(eventUpdateDto.getStateAction()).isPresent()) {
            if (eventUpdateDto.getStateAction().equals(StateAction.SEND_TO_REVIEW)) {
                    event.get().setState(EventState.PENDING);
                }
            if (eventUpdateDto.getStateAction().equals(StateAction.CANCEL_REVIEW)) {
                    event.get().setState(EventState.CANCELED);
                }
        }

        Integer confirmedRequest = requestRepository.getConfirmedRequestsByEventId(eventId);
        event.get().setConfirmedRequests(confirmedRequest);

        String eventUri = URI + event.get();
        List<ViewStatsDto> viewStatsDtos = statClient.getStats(RANGE_START, RANGE_END, List.of(eventUri), false);
        Long views = viewStatsDtos.get(0).getHits();
        return  EventMapper.toEventFullDto(eventRepository.save(event.get()),views );
    }

    //@Override
    public EventFullDto updateByUser1(Long userId, Long eventId, EventUpdateDto eventUpdateDto, HttpServletRequest request) throws ConflictException {
        Event event = eventRepository.getReferenceById(eventId);
        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Нельзя изменить событие в статусе PUBLISHED");
        }
        LocalDateTime eventDate;
        if (Optional.ofNullable(eventUpdateDto.getEventDate()).isEmpty()) {
            eventDate = event.getEventDate();
        } else {
            eventDate = LocalDateTime.parse(eventUpdateDto.getEventDate(), dateTimeFormatter);
        }
        if (eventDate.isBefore(LocalDateTime.now().plusHours(HOURS_BEFORE_START))) {
            throw new ConflictException("Время события должно быть позднее на 2 час от текущего времени");
        }

        if (Optional.ofNullable(eventUpdateDto.getAnnotation()).isPresent()) {
            event.setAnnotation(eventUpdateDto.getAnnotation());
        }
        if (Optional.ofNullable(eventUpdateDto.getCategory()).isPresent()) {
            Optional<Category> category = categoryRepository.findById(eventUpdateDto.getCategory());
            if (category.isEmpty()) {
                throw new ConflictException("Нет категории с id: " + eventUpdateDto.getCategory());
            }
            event.setCategory(category.get());
        }
        if (Optional.ofNullable(eventUpdateDto.getDescription()).isPresent()) {
            event.setDescription(eventUpdateDto.getDescription());
        }
        if (Optional.ofNullable(eventUpdateDto.getEventDate()).isPresent()) {
            event.setEventDate(LocalDateTime.parse(eventUpdateDto.getEventDate(), dateTimeFormatter));
        }
        if (Optional.ofNullable(eventUpdateDto.getLocation()).isPresent()) {
            event.setLocation(locationRepository.save(eventUpdateDto.getLocation()));
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
        if (Optional.ofNullable(eventUpdateDto.getTitle()).isPresent()) {
            event.setTitle(eventUpdateDto.getTitle());
        }
        if (!event.getState().equals(EventState.PUBLISHED) && event.getInitiator().getId().equals(userId)) {
            if (Optional.ofNullable(eventUpdateDto.getStateAction()).isPresent()) {
                if (eventUpdateDto.getStateAction().equals(StateAction.SEND_TO_REVIEW)) {
                    event.setState(EventState.PENDING);
                }
                if (eventUpdateDto.getStateAction().equals(StateAction.CANCEL_REVIEW)) {
                    event.setState(EventState.CANCELED);
                }
            }
            return toEventFullDtoWithViews(eventRepository.save(event));
        } else {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Событие должно быть в состоянии ожидания");
        }

    }

    private EventFullDto toEventFullDtoWithViews(Event event) {
        String uriEvent = URI + event.getId().toString();
        //Object responseBody = statService.getStatistics(RANGE_START, RANGE_END, List.of(uriEvent), false);
        List<ViewStatsDto> viewStatsDtos = statClient.getStats(RANGE_START, RANGE_END, List.of(uriEvent), false);
        //List<ViewStatsDto> hitDtos = statService.getStatistics(RANGE_START, RANGE_END, List.of(uriEvent), false);
        Integer views = 0;
        if (!viewStatsDtos.isEmpty()) {
            views = viewStatsDtos.size();
        }
        Integer confirmedRequests = requestRepository.getAllByEventIdAndConfirmedStatus(event.getId());
        return EventMapper.toEventFullDtoFromEvent(event,
                LocationMapper.toLocationDtoFromLocation(event.getLocation()),
                UserMapper.toUserShortDtoFromUser(event.getInitiator()),
                views,
                confirmedRequests);
    }

    private void checkAndUpdateEvent(EventUpdateDto eventUpdateDto, Event event) throws EntityNotFoundException {
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
            Optional<Category> category = categoryRepository.findById(eventUpdateDto.getCategory());
            if (category.isEmpty()) {
                throw new EntityNotFoundException("Нет категории с id: " + eventUpdateDto.getCategory());
            }
            event.setCategory(category.get());
        }
        if (Optional.ofNullable(eventUpdateDto.getEventDate()).isPresent()) {
            //DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            LocalDateTime eventDate = LocalDateTime.parse(eventUpdateDto.getEventDate(), dateTimeFormatter);
            event.setEventDate(eventDate);
        }
        if (Optional.ofNullable(eventUpdateDto.getLocation()).isPresent()) {
            event.setLocation(locationRepository.save(eventUpdateDto.getLocation()));
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

        List<Long> eventIds;
        eventIds = events.stream()
                .map(Event::getId)
                .collect(toList());

        Map<Long, Integer> confirmedRequests = requestRepository.getConfirmedRequestsByEventIds(eventIds);

        List<String> uriEventList = events.stream()
                .map(e -> URI + e.getId().toString())
                .collect(toList());

        List<ViewStatsDto> viewStatsDtos = statClient.getStats(RANGE_START, RANGE_END, uriEventList, false);

        Map<Long, Long> eventViews = getEventsMap(viewStatsDtos);

        List<EventFullDto> eventFullDtoList = new ArrayList<>();
        for (Event event : events) {
            eventFullDtoList.add(new EventFullDto(
                    event.getId(),
                    event.getTitle(),
                    event.getDescription(),
                    event.getAnnotation(),
                    event.getState(),
                    event.getCategory(),
                    event.getCreatedOn(),
                    event.getEventDate().format(dateTimeFormatter),
                    event.getPublishedOn(),
                    confirmedRequests.get(event.getId()),
                    LocationMapper.toLocationDtoFromLocation(event.getLocation()),
                    UserMapper.toUserShortDtoFromUser(event.getInitiator()),
                    event.isPaid(),
                    event.getParticipantLimit(),
                    event.isRequestModeration(),
                    eventViews.get(event.getId()))
            );
        }
        statService.addEventStat(HitMapper.toEndpointHit(APP_NAME, request));
        return eventFullDtoList;

//        List<EventFullDto> eventFullDtoList = new ArrayList<>();
//        for (Event event : events) {
//            eventFullDtoList.add(toEventFullDtoFromEvent(event, false));
//        }
//        statService.addEventStat(HitMapper.toEndpointHit(APP_NAME, request));
//        return eventFullDtoList;
    }

    @Override
    public EventFullDto updateByAdmin(Long eventId, EventUpdateDto eventUpdateDto, HttpServletRequest request) throws EntityNotFoundException, InvalidParameterException, ConflictException {
        //Event event = eventRepository.getReferenceById(eventId);
        Optional<Event> event = eventRepository.findById(eventId);
        if (event.get().getState().equals(EventState.PUBLISHED) && eventUpdateDto.getStateAction().equals(StateAction.PUBLISH_EVENT)) {
            throw new ConflictException("Нельзя опубликовать уже опубликованное событие.");
        }
        if (event.get().getState().equals(EventState.CANCELED) && eventUpdateDto.getStateAction().equals(StateAction.PUBLISH_EVENT)) {
            throw new ConflictException("Нельзя опубликовать событие в состоянии CANCELED.");
        }
        if (event.get().getState().equals(EventState.PUBLISHED) && eventUpdateDto.getStateAction().equals(StateAction.REJECT_EVENT)) {
            throw new ConflictException("Нельзя отменить событие в состоянии PUBLISHED.");
        }

        LocalDateTime startTime;
        if (Optional.ofNullable(eventUpdateDto.getEventDate()).isEmpty()) {
            startTime = event.get().getEventDate();
        } else {
            startTime = LocalDateTime.parse(eventUpdateDto.getEventDate(), dateTimeFormatter);
        }
        if (startTime.isAfter(LocalDateTime.now().plusHours(ADMIN_HOURS_BEFORE_START)) && event.get().getState().equals(EventState.PENDING)) {
            checkAndUpdateEvent(eventUpdateDto, event.get());
            if (Optional.ofNullable(eventUpdateDto.getStateAction()).isPresent()) {
                if (eventUpdateDto.getStateAction().equals(StateAction.PUBLISH_EVENT)) {
                    event.get().setState(EventState.PUBLISHED);
                    event.get().setPublishedOn(LocalDateTime.now());
                }
                if (eventUpdateDto.getStateAction().equals(StateAction.REJECT_EVENT)) {
                    event.get().setState(EventState.CANCELED);
                }
            }
            return toEventFullDtoFromEvent(eventRepository.save(event.get()), true);
        } else {
            throw new InvalidParameterException("Проверьте статус и время начала события.");
        }
    }

    private EventFullDto toEventFullDtoFromEvent(Event event, boolean updating) {
        LocationDto locationDto = LocationMapper.toLocationDtoFromLocation(event.getLocation());
        UserShortDto userShortDto = UserMapper.toUserShortDtoFromUser(event.getInitiator());
        if (updating) {
            return getEventWithoutViews(event, locationDto, userShortDto);
        } else {
            return getEventWithViews(event, locationDto, userShortDto);
        }
    }

    private EventFullDto getEventWithViews(Event event, LocationDto locationDto, UserShortDto userShortDto) {
        String uriEvent = URI + event.getId().toString();
        //Object responseBody = statService.getStatistics(RANGE_START, RANGE_END, List.of(uriEvent), false);
        List<ViewStatsDto> viewStatsDtos = statClient.getStats(RANGE_START, RANGE_END, List.of(uriEvent), false);
        //List<ViewStatsDto> viewStatsDtos = new ObjectMapper().convertValue(responseBody, new TypeReference<>() {});
        //List<ViewStatsDto> hitDtos = statService.getStatistics(RANGE_START, RANGE_END, List.of(uriEvent), false);
        Integer views = 0;
        if (!viewStatsDtos.isEmpty()) {
            views = viewStatsDtos.size();
        }
        Integer confirmedRequests = requestRepository.getAllByEventIdAndConfirmedStatus(event.getId());
        return EventMapper.toEventFullDtoFromEvent(event, locationDto, userShortDto, views, confirmedRequests);
    }

    private EventFullDto getEventWithoutViews(Event event, LocationDto locationDto, UserShortDto userShortDto) {
        Integer views = 0;
        Integer confirmedRequests = requestRepository.getAllByEventIdAndConfirmedStatus(event.getId());
        return EventMapper.toEventFullDtoFromEvent(event, locationDto, userShortDto, views, confirmedRequests);
    }
}