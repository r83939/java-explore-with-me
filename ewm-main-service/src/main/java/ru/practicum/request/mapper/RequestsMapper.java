package ru.practicum.request.mapper;

import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.model.Request;

public interface RequestsMapper {
    RequestsMapper requestMapper = Mappers.getMapper(RequestsMapper.class);

    @Mapping(target = "event", source = "event.id")
    @Mapping(target = "requester", source = "requester.id")
    RequestDto toRequestDto(Request request);
}
