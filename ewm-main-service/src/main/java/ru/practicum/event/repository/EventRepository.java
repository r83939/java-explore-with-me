package ru.practicum.event.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    @Query(value = "SELECT * FROM events AS e " +
            "WHERE (:usersIds IS NULL OR e.initiator_id IN (:usersIds)) " +
            "AND (:states IS NULL OR e.state IN (:states)) " +
            "AND (:categories IS NULL OR e.category_id IN (:categories)) " +
            "AND (CAST(:rangeStart AS date) IS NULL OR e.event_date >= :rangeStart) " +
            "AND (CAST(:rangeEnd AS date) IS NULL OR e.event_date <= :rangeEnd) " +
            "ORDER BY e.id ASC LIMIT :size OFFSET :from", nativeQuery = true)
    List<Event> searchEventsByAdmin1(@Param("usersIds") List<Long> usersIds,
                                     @Param("states") List<String> states,
                                     @Param("categories") List<Long> categories,
                                     @Param("rangeStart") LocalDateTime rangeStart,
                                     @Param("rangeEnd") LocalDateTime rangeEnd,
                                     @Param("size") Integer size,
                                     @Param("from") Integer from);

    @Query("SELECT e FROM Event AS e " +
            "WHERE (:usersIds IS NULL OR e.initiator.id IN (:usersIds)) " +
            "AND (:states IS NULL OR e.state IN (:states)) " +
            "AND (:categories IS NULL OR e.category.id IN (:categories)) " +
            "AND (CAST(:rangeStart AS date) IS NULL OR e.eventDate >= :rangeStart) " +
            "AND (CAST(:rangeEnd AS date) IS NULL OR e.eventDate <= :rangeEnd) " +
            "ORDER BY e.id ASC")
    List<Event> searchEventsByAdmin(@Param("usersIds") List<Long> usersIds,
                                    @Param("states") List<EventState> states,
                                    @Param("categories") List<Long> categories,
                                    @Param("rangeStart") LocalDateTime rangeStart,
                                    @Param("rangeEnd") LocalDateTime rangeEnd,
                                    Pageable pageable);

    @Query("SELECT e " +
            "FROM Event AS e " +
            "WHERE " +
            "(:text IS NULL " +
            "OR LOWER(e.description) LIKE LOWER(CONCAT('%', :text, '%')) " +
            "OR LOWER(e.annotation) LIKE LOWER(CONCAT('%', :text, '%'))) " +
            "AND (:categories IS NULL OR e.category.id IN (:categories)) " +
            "AND (:paid IS NULL OR e.paid = :paid) " +
            "AND (CAST(:rangeStart AS date) IS NULL OR e.eventDate >= :rangeStart) " +
            "AND (CAST(:rangeEnd AS date) IS NULL OR e.eventDate <= :rangeEnd) " +
            "AND (:onlyAvailable IS NULL OR e.confirmedRequests < e.participantLimit) " +
            "ORDER BY e.eventDate")
    List<Event> findEventsByParamsOrderByDate(
            @Param("text") String text,
            @Param("categories") List<Long> categories,
            @Param("paid") Boolean paid,
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeEnd") LocalDateTime rangeEnd,
            @Param("onlyAvailable") Boolean onlyAvailable,
            Pageable pageable);

    @Query("SELECT e " +
            "FROM Event AS e " +
            "WHERE " +
            "(:text IS NULL " +
            "OR LOWER(e.description) LIKE LOWER(CONCAT('%', :text, '%')) " +
            "OR LOWER(e.annotation) LIKE LOWER(CONCAT('%', :text, '%'))) " +
            "AND (:categories IS NULL OR e.category.id IN (:categories)) " +
            "AND (:paid IS NULL OR e.paid = :paid) " +
            "AND (CAST(:rangeStart AS date) IS NULL OR e.eventDate >= :rangeStart) " +
            "AND (CAST(:rangeEnd AS date) IS NULL OR e.eventDate <= :rangeEnd) " +
            "AND (:onlyAvailable IS NULL OR e.confirmedRequests < e.participantLimit) " +
            "ORDER BY e.id")
    List<Event> findEventsByParamsOrderById(
            @Param("text") String text,
            @Param("categories") List<Long> categories,
            @Param("paid") Boolean paid,
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeEnd") LocalDateTime rangeEnd,
            @Param("onlyAvailable") Boolean onlyAvailable,
            Pageable pageable);

    @Query(value = "SELECT * FROM events " +
            "WHERE initiator_id = ?1 " +
            "ORDER BY id DESC LIMIT ?2 OFFSET ?3", nativeQuery = true)
    List<Event> getByUserIdWithPagination(Long id, Integer size, Integer from);

    List<Event> findByInitiatorIdOrderByIdDesc(Long initiatorId, PageRequest pageable);

    List<Event> findAllByCategoryId(Long id);

    @Query("select event from Event event " +
            "where event.id IN (:ids)")
    List<Event> findByIds(@Param("ids") List<Long> ids);

}