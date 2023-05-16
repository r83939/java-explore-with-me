package ru.practicum;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Component
public class StatsWebClient {
    private final WebClient webClient;

    public StatsWebClient(WebClient webClient) {
        this.webClient = webClient;
    }


    public ResponseEntity<Object> addEvent(EndpointHitDto endpointHitDto) {
        return webClient.post()
                .uri("/hit")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(endpointHitDto))
                .exchangeToMono(response -> {
                    if (response.statusCode().equals(HttpStatus.CREATED)) {
                        return response.bodyToMono(Object.class)
                                .map(body -> ResponseEntity.status(HttpStatus.CREATED).body(body));
                    } else {
                        return response.createException()
                                .flatMap(Mono::error);
                    }
                })
                .block();
    }

    public ResponseEntity<Object> getStatistics(
            String start,
            String end,
            List<String> uris,
            Boolean unique) {
        String paramsUri = uris.stream().reduce("", (result, uri) -> result + "&uris=" + uri);

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/stats")
                        .queryParam("start", start)
                        .queryParam("end", end)
                        .query(paramsUri)
                        .queryParam("unique", unique)
                        .build())
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        return response.bodyToMono(Object.class)
                                .map(body -> ResponseEntity.ok().body(body));
                    } else {
                        return response.createException()
                                .flatMap(Mono::error);
                    }
                })
                .block();
    }

}
