package ru.practicum.user.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.user.model.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query(value = "SELECT * FROM users WHERE id IN (?1) ORDER BY id DESC LIMIT ?2 OFFSET ?3", nativeQuery = true)
    List<User> getByIds(List<Long> ownerId, Integer size, Integer from);

    boolean existsUserByEmail(String email);

    List<User> findAllByIdIn(List<Long> ids, Pageable pageable);
}