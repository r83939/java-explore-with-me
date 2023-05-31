package ru.practicum.Comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.Comment.model.Comment;


@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    Comment deleteCommentById(long id);
}
