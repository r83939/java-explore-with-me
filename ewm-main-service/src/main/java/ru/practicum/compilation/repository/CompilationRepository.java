package ru.practicum.compilation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.compilation.model.Compilation;

import java.util.List;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {

    @Query(value = "SELECT * FROM compilations WHERE pinned = ?1 ORDER BY id DESC LIMIT ?2 OFFSET ?3", nativeQuery = true)
    List<Compilation> getAllWithPagination(String pinned, Integer size, Integer from);
}