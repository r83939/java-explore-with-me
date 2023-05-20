package ru.practicum.event.dto;

import lombok.NoArgsConstructor;
import ru.practicum.category.model.Category;
import ru.practicum.event.location.LocationDto;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.user.dto.UserShortDto;

import java.time.LocalDateTime;

@NoArgsConstructor
public final class EventMapper {

    public static Event toEventFromEventNewDto(Long userId, Long locationId, EventNewDto eventNewDto) {
        return new Event(
                eventNewDto.getId(),
                eventNewDto.getTitle(),
                eventNewDto.getDescription(),
                eventNewDto.getAnnotation(),
                EventState.valueOf("PENDING"),
                eventNewDto.getCategory(),
                LocalDateTime.now(),
                LocalDateTime.parse(eventNewDto.getEventDate().replaceAll(" ", "T")),
                null,
                0,
                locationId,
                userId,
                eventNewDto.isPaid(),
                eventNewDto.getParticipantLimit(),
                eventNewDto.isRequestModeration()
        );
    }

    public static EventFullDto toEventFullDtoFromEvent(
            Event event, Category category, LocationDto locationDto, UserShortDto userShortDto, Integer views, Integer confirmedRequests) {
        return new EventFullDto(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getAnnotation(),
                event.getState(),
                category,
                event.getCreatedOn(),
                event.getEventDate().toString().replaceAll("T", " "),
                event.getPublishedOn(),
                confirmedRequests,
                locationDto,
                userShortDto,
                event.isPaid(),
                event.getParticipantLimit(),
                event.isRequestModeration(),
                views
        );
    }
    public static EventShortDto toEventShortDtoFromEvent(
            Event event, Category category, UserShortDto userShortDto, Integer views) {
        return new EventShortDto(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getAnnotation(),
                category,
                event.getEventDate().toString().replaceAll("T", " "),
                userShortDto,
                event.isPaid(),
                views
        );
    }
}