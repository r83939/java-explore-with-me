package ru.practicum.statistic;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.EndpointHitDto;
import ru.practicum.StatsWebClient;
import ru.practicum.ViewStatsDto;

import java.util.List;

@Slf4j
@Service
public class StatService {
    private final StatsWebClient statsWebClient;

    public StatService(StatsWebClient statsWebClient) {
        this.statsWebClient = statsWebClient;
    }


    public List<ViewStatsDto> getStatistics(
            String rangeStart,
            String rangeEnd,
            List<String> uris,
            Boolean unique) {
        log.info("Call#StatService#getStatistics# rangeStart={}, rangeEnd={}, uris={}, unique={}", rangeStart, rangeEnd, uris, unique);
        return statsWebClient.getStatistics(rangeStart, rangeEnd, uris, unique);
    }

    @Transactional
    public void addEventStat(EndpointHitDto endpointHitDto) {
        log.info("Call#StatService#addEventStat# endpointHitDto: {}", endpointHitDto);
        statsWebClient.addEvent(endpointHitDto);
    }
}
