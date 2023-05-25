package ru.practicum.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.request.model.Request;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {


    

    @Query(value = "SELECT * FROM participation_request WHERE requester_id = ?1 ORDER BY id", nativeQuery = true)
    List<Request> getAllByUserId(Long userId);

    @Query(value = "SELECT * FROM participation_request WHERE requester_id = ?1 AND event_id = ?2 ORDER BY id", nativeQuery = true)
    List<Request> getByUserAndEventId(Long userId, Long eventId);

    @Query(value = "SELECT COUNT(*) FROM participation_request WHERE status = 'CONFIRMED' AND event_id = ?1", nativeQuery = true)
    Integer getAllByEventIdAndConfirmedStatus(Long eventId);

    @Query(value = "SELECT r.event_id as eventId, count(*) AS confirmedRequests " +
            "FROM participation_request r " +
            "WHERE status = 'CONFIRMED' AND event_id IN (?1) " +
            "GROUP BY r.event_id", nativeQuery = true)
    Map<Long, Integer> getConfirmedRequestsByEventIds(List<Long> eventIds);

    @Query(value = "SELECT r.event_id as eventId, count(*) AS confirmedRequests " +
            "FROM participation_request r " +
            "WHERE status = 'CONFIRMED' " +
            "GROUP BY r.event_id", nativeQuery = true)
    Integer getConfirmedRequestsByEventId(Long eventId);

    @Query(value = "SELECT * FROM participation_request WHERE id IN (?1)", nativeQuery = true)
    List<Request> getByRequestsList(List<Long> requestIds);

    @Query(value = "select count(*) from participation_request pr " +
            "where pr.event_id = :eventId and pr.status = 'CONFIRMED'", nativeQuery = true)
    int findByEventIdConfirmed(@Param("eventId") Long eventId);

    @Query(value = "SELECT * FROM participation_request " +
            "WHERE event_id = ?1 AND  requester_id = ?2", nativeQuery = true)
    Optional<Request> findByEventIdAndRequesterId(Long eventId, Long userId);
}