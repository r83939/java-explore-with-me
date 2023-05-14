package ru.practicum.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.request.model.Request;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findByUserId(Long userId);

    Optional<Request> findByIdAndRequesterId(Long requestId, Long userId);

    @Query("select request from Request request " +
            "where request.event.id = :eventId " +
            "and request.requester.id = :userId")
    Optional<Request> findByEventIdAndRequesterId(@Param("eventId") Long eventId,
                                                               @Param("userId") Long userId);

    @Query("select request from Request request " +
            "where request.event.id = :eventId " +
            "and request.event.initiator.id = :userId")
    List<Request> findByEventIdAndInitiatorId(@Param("eventId") Long eventId,
                                                           @Param("userId") Long userId);

    @Query("select request from Request request " +
            "where request.event.id = :eventId and request.status = 'CONFIRMED'")
    List<Request> findByEventIdConfirmed(@Param("eventId") Long eventId);

    @Query("select request from Request request " +
            "where request.event.id = :event " +
            "and request.id IN (:requestIds)")
    List<Request> findByEventIdAndRequestsIds(@Param("event") Long eventId,
                                                           @Param("requestIds") List<Long> requestIds);




}
