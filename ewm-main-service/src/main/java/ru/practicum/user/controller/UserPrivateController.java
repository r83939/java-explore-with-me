package ru.practicum.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.user.model.User;
import ru.practicum.user.service.UserServiceImpl;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserPrivateController {

    private final UserServiceImpl userService;

    @PostMapping
    public User create(@RequestBody @Valid User user) {
        log.info("Received a request to create a new User: {}", user);
        return userService.create(user);
    }

    @GetMapping
    public List<User> getAll(List<Long> ids, Integer from, Integer size) {
        log.info("Received a request to get all Users");
        return userService.getByIds(ids, size, from);
    }

    @GetMapping("/{id}")
    public User get(@PathVariable Long id) {
        log.info("Received a request to get a user with id {}", id);
        return userService.get(id);
    }

    @PatchMapping("/{id}")
    public User update(@PathVariable Long id, @RequestBody User user) {
        log.info("Received a request to update a user with id: {}, User: {}", id, user);
        return userService.update(id, user);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        log.info("Received a request to delete a user with id: {}", id);
        userService.delete(id);
    }
}