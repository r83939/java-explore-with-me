package ru.practicum.Comment.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.Comment.dto.CommentRequestDto;
import ru.practicum.Comment.dto.CommentResponseDto;
import ru.practicum.Comment.mapper.CommentMapper;
import ru.practicum.Comment.model.Comment;
import ru.practicum.Comment.model.CommentSort;
import ru.practicum.Comment.repository.CommentRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.InvalidParameterException;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@Getter
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    @Override
    public CommentResponseDto addComment(Long userId, CommentRequestDto commentRequestDto) throws InvalidParameterException {
        log.info("Call#CommentServiceImpl#addComment# userId: {}, commentRequestDto: {}", userId, commentRequestDto);
        Optional<User> user = userRepository.findById(userId);
        Optional<Event> event = eventRepository.findById(commentRequestDto.getEventId());
        if (event.isEmpty()) {
            throw new InvalidParameterException("Нет события с id: " + commentRequestDto.getEventId());
        }
        Comment comment = new Comment();
        comment.setText(commentRequestDto.getText());
        comment.setUser(user.get());
        comment.setItem(event.get());
        comment.setCreated(LocalDateTime.now());

        return CommentMapper.toCommentResponceDto(commentRepository.save(comment));
    }

    @Override
    public CommentResponseDto updateComment(Long userId, Long commentId, CommentRequestDto commentRequestDto) throws InvalidParameterException {
        log.info("Call#CommentServiceImpl#updateComment# userId: {}, commentRequestDto: {}", userId, commentRequestDto);
        Optional<User> user = userRepository.findById(userId);
        Optional<Comment> comment = commentRepository.findById(commentId);
        if (comment.isEmpty()) {
            throw new InvalidParameterException("Нет комментария с id: " +commentId);
        }
        comment.get().setText(commentRequestDto.getText());

        return CommentMapper.toCommentResponceDto(commentRepository.save(comment.get()));
    }

    @Override
    public CommentResponseDto deleteComment(Long userId, Long commentId) throws InvalidParameterException {
        log.info("Call#CommentServiceImpl#addComment# userId: {}, commentId: {}", userId);
        Optional<User> user = userRepository.findById(userId);
        Optional<Comment> comment = commentRepository.findById(commentId);
        if (comment.isEmpty()) {
            throw new InvalidParameterException("Нет комментария с id: " +commentId);
        }
        return CommentMapper.toCommentResponceDto(commentRepository.deleteCommentById(commentId));
    }

    @Override
    public CommentResponseDto getComment(Long userId, Long commentId) {
        log.info("Call#CommentServiceImpl#deleteComment# userId: {}, commentId: {}", userId, commentId);
        return CommentMapper.toCommentResponceDto(commentRepository.getById(commentId));
    }

    @Override
    public CommentResponseDto getComments(Long eventId, String text, String rangeStart, String rangeEnd, CommentSort sort, Integer from, Integer size) {
        log.info("Call#CommentServiceImpl#getComments# userId: {}, text: {}", eventId, text);
        return null;
    }
}
