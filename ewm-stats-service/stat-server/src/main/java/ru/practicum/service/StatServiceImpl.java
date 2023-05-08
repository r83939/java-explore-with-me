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
import java.util.stream.Collectors;

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
        if (uris == null || uris.isEmpty()) {
            if (unique) {
                log.info("Запрошена статистика start: {}, end: {}, uris: {}, unique ip: {}", start, end, uris, unique);
                Thread.currentThread().getStackTrace()[1].getMethodName();
                return statRepo.getStatistics(start, end, unique).stream()
                        .map(s -> StatMapper.toViewStatsDto(s))
                        .collect(Collectors.toList());
            }
            log.info("Запрошена статистика start: {}, end: {}", start, end);
            return statRepo.getStatistics(start, end).stream()
                    .map(s -> StatMapper.toViewStatsDto(s))
                    .collect(Collectors.toList());
        } else {
            if (unique) {
                log.info("Запрошена статистика start: {}, end: {}, unique ip: {}", start, end, unique);
                return statRepo.getStatistics(start, end, uris, unique).stream()
                        .map(s -> StatMapper.toViewStatsDto(s))
                        .collect(Collectors.toList());
            }
            log.info("Запрошена статистика start: {}, end: {}, uris: {}", start, end, uris);
            return statRepo.getStatistics(start, end, uris).stream()
                    .map(s -> StatMapper.toViewStatsDto(s))
                    .collect(Collectors.toList());
        }
    }

    @Override
    public EndpointHitDto addEvent(EndpointHitDto endpointHitDto) {
        EndpointHit event = statRepo.save(StatMapper.toEndpointHit(endpointHitDto));
        log.info("Создано событие с id: {}, app: {}, uri: {}, ip: {}, timestamp: {} ",
                event.getId(), event.getApp(), event.getUri(), event.getIp(),event.getTimestamp());
        return StatMapper.toEndpointHitDto(event);
    }
}
