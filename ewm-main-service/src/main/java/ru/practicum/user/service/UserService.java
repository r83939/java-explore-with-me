package ru.practicum.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.EntityNotFoundException;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.user.mapper.UserMapper.userMapper;

@Service
@Slf4j
public class UserService {
    private final UserRepository userRepo;

    @Autowired
    public UserService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public List<UserDto> findAll(List<Long> ids, Integer from, Integer size) {
        log.info("Call UserService#findAll#");
        PageRequest page = PageRequest.of(from / size, size);
        if (!ids.isEmpty()) {
            return userRepo.findAllByIdIn(ids, page).stream()
                    .map(userMapper::toUserDto)
                    .collect(Collectors.toList());
        } else {
            return userRepo.findAll(page).stream()
                    .map(userMapper::toUserDto)
                    .collect(Collectors.toList());
        }
    }

    public UserDto createUser(UserDto userDto) {
        User user = userRepo.save(UserMapper.userMapper.toUser(userDto));
        log.info("Call UserService#createUser#Create user with id {}", user.getId());
        return userMapper.toUserDto(user);
    }

    public void deleteUser(long userId) throws EntityNotFoundException {
        Optional<User> user = userRepo.findById(userId);
        if (user.isEmpty()) {
            throw new EntityNotFoundException("User with id: " + userId + " not found");
        }
        userRepo.deleteById(userId);
        log.info("Call UserService#deleteUser#User with id {} deleted", userId);
    }
}
