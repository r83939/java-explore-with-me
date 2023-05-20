package ru.practicum.user.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.domain.UserValidator;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.DuplicateEmailException;
import ru.practicum.exception.EntityNotFoundException;
import ru.practicum.exception.InvalidParameterException;
import ru.practicum.user.repository.UserRepository;
import ru.practicum.user.model.User;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Getter
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserValidator userValidator;

    @Override
    public User addUser(User user) throws InvalidParameterException, ConflictException {
        log.info("Call#UserServiceImpl#addUser# userEmail: {}, userName: {}", user.getEmail(), user.getName());
        userValidator.newUserValidate(user);
        if (userRepository.existsUserByEmail(user.getEmail())) {
            throw new ConflictException("Уже существует пользователь с email: " + user.getEmail());
        }
        return userRepository.save(user);
    }

    @Override
    public List<User> getUserByIds(List<Long> ids, Integer from, Integer size) {
        log.info("Call#UserServiceImpl#getUserByIds# idsCount: {}, from: {}, size: {}", ids.size(), from, size);
        return userRepository.getByIds(ids, from, size);
    }

    @Override
    public User getUser(Long userId) throws EntityNotFoundException {
        log.info("Call#UserServiceImpl#getUser# UserId: {}", userId);
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new EntityNotFoundException("Нет пользователя с id: " + userId);
        }
        return user.get();
    }

    @Override
    public User updateUser(Long id, User user) throws EntityNotFoundException, DuplicateEmailException {
        log.info("Call#UserServiceImpl#updateUser# UserId: {}, userName: {}", id, user.getName());
        Optional<User> updateUser = userRepository.findById(user.getId());
        if (updateUser.isEmpty()) {
            throw new EntityNotFoundException("Нет пользователя с id: " + user.getId());
        }
        if ((user.getEmail() == null || user.getEmail().isBlank())) {
            user.setEmail(updateUser.get().getEmail());
        }
        if ((user.getName() == null || user.getName().isBlank())) {
            user.setName(updateUser.get().getName());
        }
        if (!updateUser.get().getEmail().equals(user.getEmail()) && (userRepository.existsUserByEmail(user.getEmail()))) {
            throw new DuplicateEmailException("этот еmail: " + user.getEmail() + " уже используется другим пользователем");
        }
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Long userId) throws EntityNotFoundException, InvalidParameterException {
        log.info("Call#UserServiceImpl#deleteUser# UserId: {}", userId);
            if (userId <= 0) {
                throw new InvalidParameterException("id - должно быть целым числом, вы передали " +  userId);
            }
            Optional<User> deleteUser = userRepository.findById(userId);
            if (deleteUser.isEmpty()) {
                throw new EntityNotFoundException("Нет пользователя с id: " + userId);
            }
            userRepository.deleteById(userId);
        }
}