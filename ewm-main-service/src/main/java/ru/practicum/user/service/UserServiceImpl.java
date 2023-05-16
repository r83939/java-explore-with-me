package ru.practicum.user.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.user.repository.UserRepository;
import ru.practicum.user.model.User;

import java.util.List;
import java.util.Optional;

@Service
@Getter
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User create(User user) {
        try {
            return userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User уже существует");
        }
    }

    @Override
    public List<User> getByIds(List<Long> ids, Integer from, Integer size) {
        return userRepository.getByIds(ids, from, size);
    }

    @Override
    public User get(Long id) {
        try {
            userRepository.getReferenceById(id).getName();
            return userRepository.getReferenceById(id);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User с запрошенным id не существует");
        }
    }

    @Override
    public User update(Long id, User user) {
        User curUser = userRepository.getReferenceById(id);
        if (Optional.ofNullable(user.getName()).isPresent()) {
            curUser.setName(user.getName());
        }
        if (Optional.ofNullable(user.getEmail()).isPresent()) {
            curUser.setEmail(user.getEmail());
        }
        return userRepository.save(curUser);
    }

    @Override
    public void delete(Long id) {
        try {
            userRepository.getReferenceById(id).getName();
            userRepository.deleteById(id);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User с запрошенным id не существует");
        }
    }
}