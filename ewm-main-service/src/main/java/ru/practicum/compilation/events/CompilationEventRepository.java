package ru.practicum.compilation.events;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CompilationEventRepository extends JpaRepository<CompilationEvent, Long> {

    @Modifying(clearAutomatically = true)
    @Query(value = "DELETE FROM compilations_events WHERE compilation_id = ?1", nativeQuery = true)
    void deleteByCompilationId(Long id);

    @Query(value = "SELECT event_id FROM compilations_events WHERE compilation_id = ?1", nativeQuery = true)
    List<Long> findAllByCompilationId(Long id);
}
