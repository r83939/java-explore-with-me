package ru.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.ResponseCompilationDto;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.InvalidParameterException;


import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CompilationsServiceImpl implements CompilationsService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public ResponseCompilationDto createCompilation(NewCompilationDto newCompilationDto) throws InvalidParameterException {
        log.info("Call #CompilationsService#createCompilation# newCompilationDto: {}", newCompilationDto);
        if (newCompilationDto.getTitle() == null) {
            throw new InvalidParameterException("Поле Title должно быть заполнено.");
        }
        if (newCompilationDto.getTitle().isBlank()) {
            throw new InvalidParameterException("Поле Title не должно быть пустым.");
        }
        if (newCompilationDto.getPinned() == null) {
            newCompilationDto.setPinned(false);
        }
        List<Event> events = eventRepository.findByIds(newCompilationDto.getEvents());
        return CompilationMapper.toResponseCompilationDto(
                compilationRepository.save(CompilationMapper.toCompilation(newCompilationDto, events)));
    }

    @Override
    @Transactional
    public void deleteCompilation(Long compId) {
        log.info("Call #CompilationsService#deleteCompilation# compId: {}", compId);
        compilationRepository.findById(compId).orElseThrow();
        compilationRepository.deleteById(compId);
    }

    @Override
    @Transactional
    public ResponseCompilationDto updateCompilation(Long compId, NewCompilationDto newCompilationDto) {
        log.info("Call #CompilationsService#updateCompilation# compId: {}, newCompilationDto: {}", compId, newCompilationDto);
        Compilation compilation = compilationRepository.findById(compId).orElseThrow();
        if (newCompilationDto.getEvents() != null) {
            compilation.setEvents(eventRepository.findByIds(newCompilationDto.getEvents()));
        }
        if (newCompilationDto.getPinned() != null) {
            compilation.setPinned(newCompilationDto.getPinned());
        }
        if (newCompilationDto.getTitle() != null) {
            compilation.setTitle(newCompilationDto.getTitle());
        }
        compilationRepository.save(compilation);
        return CompilationMapper.toResponseCompilationDto(compilation);
    }

    @Override
    public List<ResponseCompilationDto> findAllCompilations(Boolean pinned, Pageable pageable) {
        log.info("Call #CompilationsService#findAllCompilations# pinned: {}, pageable: {}", pinned, pageable);
            return compilationRepository.findAllByPinned(pinned, pageable).stream()
                    .map(CompilationMapper::toResponseCompilationDto)
                    .collect(Collectors.toList());

    }

    @Override
    public ResponseCompilationDto findCompilationsById(Long compId) {
        log.info("Call #CompilationsService#findCompilationsById# compId: {}", compId);
        Compilation compilation = compilationRepository.findById(compId).orElseThrow();
        return CompilationMapper.toResponseCompilationDto(compilation);
    }
}
