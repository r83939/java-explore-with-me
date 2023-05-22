package ru.practicum.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.DuplicateEmailException;
import ru.practicum.exception.EntityNotFoundException;
import ru.practicum.exception.InvalidParameterException;
import ru.practicum.user.model.User;
import ru.practicum.user.service.UserServiceImpl;

import javax.validation.Valid;
import java.util.List;

@Validated
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserServiceImpl userService;

    @PostMapping
    public User addUser(@RequestBody @Valid User user) throws InvalidParameterException, ConflictException {
        log.info("Call#UserController#addUser# userEmail {}, userName: {}: ", user.getEmail(), user.getName());
        return userService.addUser(user);
    }

    @GetMapping
    public List<User> getUsersByIds(List<Long> ids, Integer from, Integer size) {
        log.info("Call#UserController#getUserByIds# ids {}:  size {} from {} ", ids, size, from);
        return userService.getUserByIds(ids, size, from);
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) throws EntityNotFoundException {
        log.info("Call#UserController#getUserById# id: {}", id);
        return userService.getUser(id);
    }

    @PatchMapping("/{id}")
    public User updateUser(@PathVariable Long id, @Valid @RequestBody User user) throws DuplicateEmailException, EntityNotFoundException {
        log.info("Call#UserController#updateUser# userId {}: ", id);
        return userService.updateUser(id, user);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) throws InvalidParameterException, EntityNotFoundException {
        log.info("Call#UserController#deleteUser# userId {}: ", id);
        userService.deleteUser(id);
    }
}