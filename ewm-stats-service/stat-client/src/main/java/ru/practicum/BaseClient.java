package ru.practicum;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;


import java.util.List;
import java.util.Map;

public class BaseClient {
    protected final RestTemplate rest;

    public BaseClient(RestTemplate rest) {
        this.rest = rest;
    }

    protected <T> void post(String path, T body) {
        makeAndSendRequest(HttpMethod.POST, path, null, body);
    }

    protected ResponseEntity<List<ViewStatsDto>> get(String path, Map<String, Object> parameters) {
        HttpEntity<Object> requestEntity = new HttpEntity<>(null, defaultHeaders());
        ResponseEntity<List<ViewStatsDto>> statsServerResponse;
        try {
            statsServerResponse = rest.exchange(path, HttpMethod.GET, requestEntity, new ParameterizedTypeReference<>() {
            }, parameters);
        } catch (HttpStatusCodeException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
        return prepareEventClientResponse2(statsServerResponse);
    }

    private ResponseEntity<Object> prepareEventClientResponse(ResponseEntity<Object> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }
        ResponseEntity.BodyBuilder bodyBuilder = ResponseEntity.status((response.getStatusCode()));
        if (response.hasBody()) {
            return bodyBuilder.body(response.getBody());
        }
        return bodyBuilder.build();
    }

    private ResponseEntity<List<ViewStatsDto>> prepareEventClientResponse2(ResponseEntity<List<ViewStatsDto>> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }
        ResponseEntity.BodyBuilder bodyBuilder = ResponseEntity.status((response.getStatusCode()));
        if (response.hasBody()) {
            return bodyBuilder.body(response.getBody());
        }
        return bodyBuilder.build();
    }

    private <T> ResponseEntity<Object> makeAndSendRequest(
            HttpMethod method, String path, @Nullable Map<String, Object> parameters, @Nullable T body) {
        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders());
        ResponseEntity<Object> statsServerResponse;
        try {
            if (parameters != null) {
                statsServerResponse = rest.exchange(path, method, requestEntity, Object.class, parameters);
            } else {
                statsServerResponse = rest.exchange(path, method, requestEntity, Object.class);
            }
        } catch (HttpStatusCodeException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getResponseBodyAsByteArray());
        }
        return prepareEventClientResponse(statsServerResponse);
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }
}
