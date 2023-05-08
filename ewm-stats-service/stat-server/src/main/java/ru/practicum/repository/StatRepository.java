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
            "WHERE h.timestamp BETWEEN :start AND :end h.uri AND h.uri IN :uris " +
            "GROUP BY COUNT h.app, h.uri " +
            "ORDER BY COUNT(DISTINCT h.ip) DESC", nativeQuery = true)
    List<ViewStats> getStatistics(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("uris") List<String> uris,
            String unique);
    @Query(value = "SELECT h.app, h.uri, count(h.ip) FROM hits h " +
            "WHERE h.timestamp BETWEEN :start AND :end AND h.uri IN :uris " +
            "GROUP BY COUNT h.app, h.uri " +
            "ORDER BY COUNT(h.ip) DESC", nativeQuery = true)
    List<ViewStats> getStatistics(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("uris") List<String> uris);

    @Query(value = "SELECT h.app, h.uri, count(DISTINCT h.ip) FROM hits h " +
            "WHERE h.timestamp BETWEEN :start AND :end " +
            "GROUP BY COUNT h.app, h.uri " +
            "ORDER BY COUNT(DISTINCT h.ip) DESC", nativeQuery = true)
    List<ViewStats> getStatistics(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            String unique);

    @Query(value = "SELECT h.app, h.uri, count(h.ip) FROM hits h " +
            "WHERE h.timestamp BETWEEN :start AND :end " +
            "GROUP BY COUNT h.app, h.uri " +
            "ORDER BY COUNT(h.ip) DESC", nativeQuery = true)
    List<ViewStats> getStatistics(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

}
