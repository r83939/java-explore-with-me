package ru.practicum.event.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventUpdateRequestDto;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.request.model.EventState;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.statistic.StatService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.event.mapper.EventMapper.eventMapper;

@Slf4j
@Service
public class AdminEventService {
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final EventRepository eventRepo;
    private final CategoryRepository categoryRepo;
    private final RequestRepository requestsRepo;
    private final StatService statService;

    public AdminEventService(EventRepository eventRepo, CategoryRepository categoryRepo, RequestRepository requestsRepo, StatService statService) {
        this.eventRepo = eventRepo;
        this.categoryRepo = categoryRepo;
        this.requestsRepo = requestsRepo;
        this.statService = statService;
    }

    public List<EventFullDto> getEvents(List<Long> users, List<EventState> states, List<Long> categories, String rangeStart, String rangeEnd, Integer from, Integer size) {
        List<EventFullDto> fullEventDtoList = criteriaEventRepository.findEvents(users, states, categories, rangeStart, rangeEnd, from, size)
                .stream()
                .map(eventMapper::toFullEventDto)
                .collect(Collectors.toList());
        EventUtil.getConfirmedRequests(fullEventDtoList, requestsRepository);
        return EventUtil.getViews(fullEventDtoList, statService);
    }

    public EventFullDto updateEvent(Long eventId, EventUpdateRequestDto eventUpdateRequestDto) {
        Event event = eventRepo.findById(eventId).orElseThrow(() -> {
            throw new ObjectNotFoundException("Event not found");
        });
        if (eventUpdateRequestDto.getEventDate() != null) {
            if (LocalDateTime.parse(eventUpdateRequestDto.getEventDate(),
                    dateTimeFormatter).isBefore(LocalDateTime.now())) {
                throw new ConflictException("Date in the past");
            } else {
                event.setEventDate(LocalDateTime.parse(eventUpdateRequestDto.getEventDate(),
                        dateTimeFormatter));
            }
        }
        if (event.getEventState() == EventState.PUBLISHED
                && eventUpdateRequestDto.getStateAction().equalsIgnoreCase(AdminStateAction.PUBLISH_EVENT.name())) {
            throw new ConflictException("Event is already published");
        }
        if (event.getEventState() == EventState.CANCELED
                && eventUpdateRequestDto.getStateAction().equalsIgnoreCase(AdminStateAction.PUBLISH_EVENT.name())) {
            throw new ConflictException("Event is canceled");
        }
        if (event.getEventState() == EventState.PUBLISHED
                && eventUpdateRequestDto.getStateAction().equalsIgnoreCase(AdminStateAction.REJECT_EVENT.name())) {
            throw new ConflictException("Event is published. You can't reject it");
        }
        if (eventUpdateRequestDto.getStateAction() != null) {
            if (eventUpdateRequestDto.getStateAction().equals(AdminStateAction.PUBLISH_EVENT.name())) {
                event.setEventState(EventState.PUBLISHED);
            } else if (eventUpdateRequestDto.getStateAction().equals(AdminStateAction.REJECT_EVENT.name())
                    && event.getEventState() != EventState.PUBLISHED) {
                event.setEventState(EventState.CANCELED);
            }
        }
        if (eventUpdateRequestDto.getCategory() != null) {
            Category category = categoryRepository.findById(eventUpdateRequestDto.getCategory()).orElseThrow(() -> {
                throw new ObjectNotFoundException("Category not found for update");
            });
            event.setCategory(category);
        }
        if (eventUpdateRequestDto.getLocation() != null) {
            event.setLocation(locationRepository.save(eventUpdateRequestDto.getLocation()));
        }
        EventUtil.toEventFromUpdateRequestDto(event, eventUpdateRequestDto);
        eventRepository.save(event);
        FullEventDto fullEventDto = EventMapper.EVENT_MAPPER.toFullEventDto(event);
        EventUtil.getConfirmedRequests(Collections.singletonList(fullEventDto), requestsRepository);
        return EventUtil.getViews(Collections.singletonList(fullEventDto), statService).get(0);
    }
}
