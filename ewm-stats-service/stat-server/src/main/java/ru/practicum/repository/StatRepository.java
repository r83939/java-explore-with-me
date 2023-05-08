package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.model.EndpointHit;
import ru.practicum.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatRepository extends JpaRepository<EndpointHit, Long> {

    @Query(value = "SELECT h.app, h.uri, count(DISTINCT h.ip) FROM hits h " +
            "WHERE h.timestamp BETWEEN ?1 AND ?2 AND h.uri IN ?3 " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT(DISTINCT h.ip) DESC", nativeQuery = true)
    List<ViewStats> getStatistics(
            LocalDateTime start,
            LocalDateTime end,
            List<String> uris,
            Boolean unique);
    @Query(value = "SELECT h.app, h.uri, count(h.ip) FROM hits h " +
            "WHERE h.timestamp BETWEEN :start AND :end AND h.uri IN :uris " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT(h.ip) DESC", nativeQuery = true)
    List<ViewStats> getStatistics(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("uris") List<String> uris);

    @Query(value = "SELECT h.app, h.uri, count(DISTINCT h.ip) FROM hits h " +
            "WHERE h.timestamp BETWEEN ?1 AND ?2 " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT(DISTINCT h.ip) DESC", nativeQuery = true)
    List<ViewStats> getStatistics(
            LocalDateTime start,
            LocalDateTime end,
            Boolean unique);

    @Query(value = "SELECT h.app, h.uri, count(h.ip) FROM hits h " +
            "WHERE h.timestamp BETWEEN :start AND :end " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT(h.ip) DESC", nativeQuery = true)
    List<ViewStats> getStatistics(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

}
