package ru.practicum.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.request.model.Request;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {



    @Query(value = "SELECT * FROM participation_request WHERE requester_id = ?1 ORDER BY id", nativeQuery = true)
    List<Request> getAllByUserId(Long userId);

    @Query(value = "SELECT * FROM participation_request WHERE requester_id = ?1 AND event_id = ?2 ORDER BY id", nativeQuery = true)
    List<Request> getByUserAndEventId(Long userId, Long eventId);

    @Query(value = "SELECT COUNT(*) FROM participation_request WHERE status = 'CONFIRMED' AND event_id = ?1", nativeQuery = true)
    Integer getAllByEventIdAndConfirmedStatus(Long eventId);

    @Query(value = "SELECT * FROM participation_request WHERE id IN (?1)", nativeQuery = true)
    List<Request> getByRequestsList(List<Long> requestIds);
}