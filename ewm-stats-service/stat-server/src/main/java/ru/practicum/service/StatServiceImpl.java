package ru.practicum.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;
import ru.practicum.mapper.StatMapper;
import ru.practicum.model.EndpointHit;
import ru.practicum.repository.StatRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class StatServiceImpl implements StatService{

    private final StatRepository statRepo;

    @Autowired
    public StatServiceImpl(StatRepository statRepo) {
        this.statRepo = statRepo;
    }

    @Override
    public List<ViewStatsDto> getStatistics(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        return null;
    }

    @Override
    public EndpointHitDto addEvent(EndpointHitDto endpointHitDto) {
        EndpointHit event = statRepo.save(StatMapper.toEndpointHit(endpointHitDto));
        log.info("Создано событие с id: {}", event.getId());
        return StatMapper.toEndpointHitDto(event);
    }
}
