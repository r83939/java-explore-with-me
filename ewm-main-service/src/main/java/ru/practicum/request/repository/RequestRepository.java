package ru.practicum.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.request.model.ConfirmedRequest;
import ru.practicum.request.model.Request;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {

    @Query(value = "SELECT * FROM participation_request WHERE requester_id = ?1 ORDER BY id", nativeQuery = true)
    List<Request> getAllByUserId(Long userId);

    @Query(value = "SELECT * FROM participation_request pr " +
            "INNER JOIN events e ON pr.event_id = e.id " +
            "WHERE pr.event_id = :eventId AND e.initiator_id = :userId AND pr.requester_id <> :userId", nativeQuery = true)
    List<Request> getByUserAndEventId(@Param("userId")Long userId,
                                      @Param("eventId")Long eventId);

    @Query(value = "SELECT COUNT(*) " +
            "FROM participation_request " +
            "WHERE status = 'CONFIRMED' " +
            "AND event_id = ?1", nativeQuery = true)
    Integer getAllByEventIdAndConfirmedStatus(Long eventId);

    @Query(value = "SELECT count(*) " +
            "FROM participation_request r " +
            "WHERE r.event_id = :eventId " +
            "AND r.status = 'CONFIRMED'", nativeQuery = true)
    Integer getConfirmedRequestsByEventId(@Param("eventId") Long eventId);

    @Query("SELECT new  ru.practicum.request.model.ConfirmedRequest(r.event, count(*)) " +
            "FROM Request r " +
            "WHERE r.status = 'CONFIRMED' " +
            "AND r.event IN (:eventIds) " +
            "GROUP BY r.id")
    List<ConfirmedRequest> getConfirmedRequestsByEventIds(@Param("eventIds") List<Long> eventIds);




    @Query(value = "SELECT * FROM participation_request WHERE id IN (?1)", nativeQuery = true)
    List<Request> getByRequestsList(List<Long> requestIds);

//    @Query(value = "SELECT count(*)" +
//            " FROM participation_request pr " +
//            "where pr.event_id = :eventId " +
//            "and pr.status = 'CONFIRMED'", nativeQuery = true)
//    int findByEventIdConfirmed(@Param("eventId") Long eventId);

    @Query(value = "SELECT * FROM participation_request " +
            "WHERE event_id = ?1 AND  requester_id = ?2", nativeQuery = true)
    Optional<Request> findByEventIdAndRequesterId(Long eventId, Long userId);

}