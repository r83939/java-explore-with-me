package ru.practicum.request.mapper;

import lombok.NoArgsConstructor;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.model.Request;

@NoArgsConstructor
public class RequestMapper {

    public static RequestDto toRequestDtoFromRequest(Request request) {
        return new RequestDto(
                request.getId(),
                request.getCreated(),
                request.getEvent(),
                request.getRequester(),
                request.getStatus()
        );
    }
}