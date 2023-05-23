package ru.practicum.user.service;

import ru.practicum.exception.ConflictException;
import ru.practicum.exception.DuplicateEmailException;
import ru.practicum.exception.EntityNotFoundException;
import ru.practicum.exception.InvalidParameterException;
import ru.practicum.user.model.User;

import java.util.List;

public interface UserService {

    User addUser(User user) throws InvalidParameterException, ConflictException;

    List<User> getUserByIds(List<Long> ids, Integer size, Integer from);

    User getUser(Long id) throws EntityNotFoundException;

    User updateUser(Long id, User user) throws EntityNotFoundException, DuplicateEmailException;

    User deleteUser(Long id) throws EntityNotFoundException, InvalidParameterException;
}