package ru.practicum.comment.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.model.CommentState;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    Comment deleteById(long id);

    @Query("SELECT c " +
            "FROM Comment AS c " +
            "WHERE " +
            ":text IS NULL OR LOWER(c.text) LIKE LOWER(CONCAT('%', :text, '%')) " +
            "AND (:eventId IS NULL OR c.event.id = :eventId) " +
            "AND (CAST(:rangeStart AS date) IS NULL OR c.created >= :rangeStart) " +
            "AND (CAST(:rangeEnd AS date) IS NULL OR c.created <= :rangeEnd) " +
            "AND c.commentState = :commentState " +
            "ORDER BY :sort")
    List<Comment> getComments(@Param("eventId") Long eventId,
                                         @Param("text") String text,
                                         @Param("rangeStart") LocalDateTime rangeStart,
                                         @Param("rangeEnd") LocalDateTime rangeEnd,
                                         @Param("sort") String sort,
                                         @Param("commentState") CommentState commentState,
                                         Pageable pageable);

    @Query("UPDATE Comment c " +
            "SET c.commentState = :commentState " +
            "WHERE c.id = :commentId")
    void updateCommentState(@Param("commentId") Long commentId,
                            @Param("commentState") CommentState commentState);
}
