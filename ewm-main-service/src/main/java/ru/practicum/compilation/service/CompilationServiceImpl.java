package ru.practicum.compilation.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import ru.practicum.ViewStatsDto;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.compilation.events.CompilationEvent;
import ru.practicum.compilation.events.CompilationEventRepository;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.model.CompilationFullDto;
import ru.practicum.compilation.model.CompilationMapper;
import ru.practicum.compilation.model.CompilationNewDto;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.service.EventRepository;
import ru.practicum.event.service.StatsClient;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.repository.UserRepository;
import ru.practicum.user.dto.UserShortDto;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Getter
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final CompilationEventRepository compilationEventRepository;
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final StatsClient statsClient;

    @Override
    public CompilationFullDto create(CompilationNewDto compilationNewDto) {
        Compilation compilation = CompilationMapper.toCompilationFromCompilationNewDto(compilationNewDto);
        return getCompilationFullDto(compilationNewDto, compilation);
    }

    private CompilationFullDto getCompilationFullDto(CompilationNewDto compilationNewDto, Compilation compilation) {
        List<EventShortDto> events = new ArrayList<>();
        Compilation savedCompilation = compilationRepository.save(compilation);
        for (Long eventId : compilationNewDto.getEvents()) {
            Event event = eventRepository.getReferenceById(eventId);
            getEventShortDto(events, event);
            compilationEventRepository.save(new CompilationEvent(
                    null,
                    savedCompilation.getId(),
                    eventId));
        }
        return CompilationMapper.toCompilationFullDtoFromCompilation(savedCompilation, events);
    }

    @Override
    public List<CompilationFullDto> getAllWithPagination(String pinned, Integer size, Integer from) {
        List<CompilationFullDto> compilationFullDtoList = new ArrayList<>();
        for (Compilation compilation : compilationRepository.getAllWithPagination(pinned, size, from)) {
            List<Long> eventsIds = compilationEventRepository.findAllByCompilationId(compilation.getId());
            List<EventShortDto> eventShortDtoList = getEventShortDtoList(eventsIds);
            CompilationFullDto compilationFullDto = CompilationMapper.toCompilationFullDtoFromCompilation(
                    compilation, eventShortDtoList);
            compilationFullDtoList.add(compilationFullDto);
        }
        return compilationFullDtoList;
    }

    private List<EventShortDto> getEventShortDtoList(List<Long> eventsIds) {
        List<EventShortDto> eventShortDtoList = new ArrayList<>();
        List<Event> events = eventRepository.getByEventsIds(eventsIds);
        for (Event event : events) {
            getEventShortDto(eventShortDtoList, event);
        }
        return eventShortDtoList;
    }

    private void getEventShortDto(List<EventShortDto> eventShortDtoList, Event event) {
        Category category = categoryRepository.getReferenceById(event.getCategoryId());
        UserShortDto userShortDto = UserMapper.toUserShortDtoFromUser(userRepository.getReferenceById(event.getInitiatorId()));
        String uriEvent = "/events/" + event.getId().toString();
        List<ViewStatsDto> hitDtos = statsClient.getStats(uriEvent);
        Long views = 0L;
        if (!hitDtos.isEmpty()) {
            views = hitDtos.get(0).getHits();
        }
        eventShortDtoList.add(EventMapper.toEventShortDtoFromEvent(event, category, userShortDto, views));
    }

    @Override
    public CompilationFullDto get(Long id) {
        try {
            compilationRepository.getReferenceById(id).getTitle();
            List<Long> eventsIds = compilationEventRepository.findAllByCompilationId(id);
            List<EventShortDto> eventShortDtoList = getEventShortDtoList(eventsIds);
            CompilationFullDto compilationFullDto = CompilationMapper.toCompilationFullDtoFromCompilation(
                    compilationRepository.getReferenceById(id), eventShortDtoList);
            return compilationFullDto;

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category с запрошенным id не существует");
        }
    }

    @Override
    public CompilationFullDto update(Long id, CompilationNewDto compilationNewDto) {
        Compilation compilation = compilationRepository.getReferenceById(id);
        if (Optional.ofNullable(compilationNewDto.getTitle()).isPresent()) {
            compilation.setTitle(compilationNewDto.getTitle());
        }
        if (Optional.ofNullable(compilationNewDto.getPinned()).isPresent()) {
            compilation.setPinned(compilationNewDto.getPinned());
        }
        return getCompilationFullDto(compilationNewDto, compilation);
    }

    @Override
    public void delete(Long id) {
        try {
            compilationRepository.getReferenceById(id).getTitle();
            if (!compilationEventRepository.findAllByCompilationId(id).isEmpty()) {
                compilationEventRepository.deleteByCompilationId(id);
            }
            compilationRepository.deleteById(id);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Compilation с запрошенным id не существует");
        }
    }
}