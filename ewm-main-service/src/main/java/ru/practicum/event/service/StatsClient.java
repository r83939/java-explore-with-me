package ru.practicum.event.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.practicum.statistic.HitDto;


import java.util.Arrays;
import java.util.List;

@Component
public class StatsClient {

    @Value("${stats-server.url}")
    private String serverUrl;
    private static final String API_PREFIX = "/stats" + "?uris=";

    private static final String APP = "ewm-main-service";

    public List<HitDto> getStats(String uris) {
        RestTemplate rest = new RestTemplate();
        HitDto[] forNow = rest.getForObject(serverUrl + API_PREFIX + uris + "&app=" + APP, HitDto[].class);
        return Arrays.asList(forNow);
    }
}