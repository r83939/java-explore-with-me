package ru.practicum.compilation.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


import ru.practicum.ViewStatsDto;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.compilation.events.CompilationEvent;
import ru.practicum.compilation.events.CompilationEventRepository;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.dto.CompilationFullDto;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.dto.CompilationNewDto;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.statistic.StatService;
import ru.practicum.user.dto.UserShortDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.repository.UserRepository;


import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Getter
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private static final String RANGE_START = "2000-01-01 00:01:01";
    private static final String RANGE_END = "2099-01-01 23:59:59";
    private static final String APP_NAME = "ewm-main-service";
    private static final String URI = "/events/";

    private final CompilationRepository compilationRepository;
    private final CompilationEventRepository compilationEventRepository;
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final StatService statService;

    @Override
    public CompilationFullDto addCompilation(CompilationNewDto compilationNewDto, HttpServletRequest request) {
        log.info("Call#CompilationServiceImpl#create# compilationNewDto: {}", compilationNewDto);
        Compilation compilation = CompilationMapper.toCompilationFromCompilationNewDto(compilationNewDto);
        return getCompilationFullDto(compilationNewDto, compilation, request);
    }

    @Override
    public List<CompilationFullDto> getAllWithPagination(String pinned, Integer size, Integer from, HttpServletRequest request) {
        log.info("Call#CompilationServiceImpl#getAllWithPagination# pinned: {}, size: {}, from: {}", pinned, size, from);
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

    @Override
    public CompilationFullDto getCompilation(Long compilationId) throws ru.practicum.exception.EntityNotFoundException {
        log.info("Call#CompilationServiceImpl#get# compilationId: {},", compilationId);
        Optional<Compilation> compilation = compilationRepository.findById(compilationId);
        if (compilation.isEmpty()) {
            throw new ru.practicum.exception.EntityNotFoundException("Нет Compilation c id: " + compilationId);
        }
        List<Long> eventsIds = compilationEventRepository.findAllByCompilationId(compilationId);
        List<EventShortDto> eventShortDtoList = getEventShortDtoList(eventsIds);
        CompilationFullDto compilationFullDto = CompilationMapper.toCompilationFullDtoFromCompilation(
                compilationRepository.getReferenceById(compilationId), eventShortDtoList);
        return compilationFullDto;
    }

    @Override
    public CompilationFullDto updateCompilation(Long compilationId, CompilationNewDto compilationNewDto, HttpServletRequest request) {
        log.info("Call#CompilationServiceImpl#update# compilationId: {},", compilationId);
        Compilation compilation = compilationRepository.getReferenceById(compilationId);
        if (Optional.ofNullable(compilationNewDto.getTitle()).isPresent()) {
            compilation.setTitle(compilationNewDto.getTitle());
        }
        if (Optional.ofNullable(compilationNewDto.getPinned()).isPresent()) {
            compilation.setPinned(compilationNewDto.getPinned());
        }
        return getCompilationFullDto(compilationNewDto, compilation, request);
    }

    @Override
    public void deleteCompilation(Long compilationId) {
        log.info("Call#CompilationServiceImpl#delete# compilationId: {},", compilationId);
        Optional<Compilation> compilation = compilationRepository.findById(compilationId);
        if (compilation.isEmpty()) {
            throw new EntityNotFoundException("Нет Compilation c id: {}" + compilationId);
        }
        compilationRepository.deleteById(compilationId);
    }

    private CompilationFullDto getCompilationFullDto(CompilationNewDto compilationNewDto, Compilation compilation, HttpServletRequest request) {
        List<EventShortDto> eventShortDtoList = new ArrayList<>();
        Compilation savedCompilation = compilationRepository.save(compilation);
        for (Long eventId : compilationNewDto.getEvents()) {
            Event event = eventRepository.getReferenceById(eventId);
            getEventShortDto(eventShortDtoList, event);
            compilationEventRepository.save(new CompilationEvent(
                    null,
                    savedCompilation.getId(),
                    eventId));
        }
        return CompilationMapper.toCompilationFullDtoFromCompilation(savedCompilation, eventShortDtoList);
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
        UserShortDto userShortDto = UserMapper.toUserShortDtoFromUser(event.getInitiator());
        String uriEvent = URI + event.getId().toString();
        List<ViewStatsDto> hitDtos =  statService.getStatistics(RANGE_START, RANGE_END, List.of(uriEvent), false);
        Integer viewsCount = 0;
        if (!hitDtos.isEmpty()) {
            viewsCount = hitDtos.size();
        }
        eventShortDtoList.add(EventMapper.toEventShortDtoFromEvent(event, userShortDto, viewsCount));
    }
}