package ru.practicum.statistic;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.EndpointHitDto;
import ru.practicum.StatsWebClient;

import java.util.List;

@Slf4j
@Service

public class StatService {
    private final StatsWebClient statsWebClient;

    public StatService(StatsWebClient statsWebClient) {
        this.statsWebClient = statsWebClient;
    }


    public ResponseEntity<Object> getStatistics(
            String rangeStart,
            String rangeEnd,
            List<String> uris,
            Boolean unique) {
        log.info("Call StatsService#getStatistics# rangeStart={}, rangeEnd={}, uris={}, unique={}", rangeStart, rangeEnd, uris, unique);
        return statsWebClient.getStatistics(rangeStart, rangeEnd, uris, unique);
    }

    @Transactional
    public void addEvent(EndpointHitDto endpointHitDto) {
        log.info("Call StatsService#addEvent# endpointHitDto: {}", endpointHitDto);
        statsWebClient.addEvent(endpointHitDto);
    }
}
