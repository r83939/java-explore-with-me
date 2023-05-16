package ru.practicum.user.mapper;

import ru.practicum.user.dto.UserShortDto;
import ru.practicum.user.model.User;

public class UserMapper {

    public UserMapper() {

    }

    public static UserShortDto toUserShortDtoFromUser(User user) {
        return new UserShortDto(
                user.getId(),
                user.getName()
        );
    }
}