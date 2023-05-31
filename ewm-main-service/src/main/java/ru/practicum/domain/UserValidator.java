package ru.practicum.domain;

import org.springframework.stereotype.Component;
import ru.practicum.exception.InvalidParameterException;
import ru.practicum.user.model.User;

@Component
public class UserValidator {
    public void newUserValidate(User user) throws InvalidParameterException {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new InvalidParameterException("поле email должно быть заполнено.");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            throw new InvalidParameterException("поле name должно быть заполнено.");
        }
    }
}
