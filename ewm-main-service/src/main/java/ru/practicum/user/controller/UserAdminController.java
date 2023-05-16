package ru.practicum.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.user.model.User;
import ru.practicum.user.service.UserServiceImpl;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/users")
public class UserAdminController {

    private final UserServiceImpl userService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public User create(@RequestBody @Valid User user) {
        log.info("Received a request to create a new User: {}", user);
        return userService.create(user);
    }

    @GetMapping
    public List<User> getByIds(@RequestParam(defaultValue = "") List<Long> ids,
                               @RequestParam(defaultValue = "10") @Positive Integer size,
                               @RequestParam(defaultValue = "0") @Positive Integer from) {
        System.out.println("ids = " + ids);
        log.info("Received a request to get Users {} size {} from {} ", ids, size, from);
        return userService.getByIds(ids, size, from);
    }

    @PatchMapping("/{id}")
    public User update(@PathVariable Long id, @RequestBody User user) {
        log.info("Received a request to update a User with id: {}, user: {}", id, user);
        return userService.update(id, user);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        log.info("Received a request to delete a User with id: {}", id);
        userService.delete(id);
    }
}