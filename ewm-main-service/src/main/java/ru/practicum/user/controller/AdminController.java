package ru.practicum.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.DuplicateEmailException;
import ru.practicum.exception.EntityNotFoundException;
import ru.practicum.exception.InvalidParameterException;
import ru.practicum.user.model.User;
import ru.practicum.user.service.UserServiceImpl;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/users")
public class AdminController {

    private final UserServiceImpl userService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public User addUser(@RequestBody @Valid User user) throws InvalidParameterException, ConflictException {
        log.info("Call#AdminController#addUser# userEmail {}, userName: {}: ", user.getEmail(), user.getName());
        return userService.addUser(user);
    }

    @GetMapping
    public List<User> getUsersByIds(@RequestParam(defaultValue = "") List<Long> ids,
                               @RequestParam(defaultValue = "10") @Positive Integer size,
                               @RequestParam(defaultValue = "0") @Positive Integer from) {
        System.out.println("ids = " + ids);
        log.info("Call#AdminController#getUsersByIds# ids {}:  size {} from {} ", ids, size, from);
        return userService.getUserByIds(ids, size, from);
    }

    @PatchMapping("/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User user) throws DuplicateEmailException, EntityNotFoundException {
        log.info("Call#AdminController#updateUser# userId {}: ", id);
        return userService.updateUser(id, user);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) throws EntityNotFoundException, InvalidParameterException {
        log.info("Call#AdminController#deleteUser# userId {}: ", id);
        userService.deleteUser(id);
    }
}