package ru.practicum;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class StatClient {
    private final String serverUrl;
    private final RestTemplate restTemplate;

    public StatClient(@Value("${stats-server.url}") String serverUrl, RestTemplate restTemplate) {
        this.serverUrl = serverUrl;
        this.restTemplate = restTemplate;
    }

    public void addStats(EndpointHitDto endpointHitDto) {
        log.info("Call#StatClient#addStatsStat# endpointHitDto: {}", endpointHitDto);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<EndpointHitDto> requestEntity = new HttpEntity<>(endpointHitDto, headers);
        restTemplate.exchange(serverUrl + "/hit", HttpMethod.POST, requestEntity, EndpointHitDto.class);
    }

    public List<ViewStatsDto> getStats(String rangeStart, String rangeEnd, List<String> uris, Boolean unique) {
        log.info("Call#StatClient#getStats# rangeStart={}, rangeEnd={}, uris={}, unique={}", rangeStart, rangeEnd, uris, unique);

        StringBuilder uriBuilder = new StringBuilder(serverUrl + "/stats?start={start}&end={end}");
        Map<String, Object> parameters = Map.of(
                "start", rangeStart,
                "end", rangeEnd);

        if (uris != null && !uris.isEmpty()) {
            for (String uri : uris) {
                uriBuilder.append("&uris=").append(uri);
            }
        }

        if (unique != null) {
            uriBuilder.append("&unique=").append(unique);
        }

        Object responseBody = restTemplate.getForEntity(
                uriBuilder.toString(),
                Object.class, parameters).getBody();

        List<ViewStatsDto> stats = new ArrayList<>();
        if (responseBody != null) {

            List<Map<String, Object>> body = (List<Map<String, Object>>) responseBody;
            if (body != null && body.size() > 0) {
                for (Map<String, Object> s : body) {
                    ViewStatsDto viewStats = ViewStatsDto.builder()
                            .app(s.get("app").toString())
                            .uri(s.get("uri").toString())
                            .hits(((Number) s.get("hits")).longValue())
                            .build();
                    stats.add(viewStats);
                }
            }
        }
        return stats;
    }
}