package ru.practicum.Comment.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.Comment.dto.CommentRequestDto;
import ru.practicum.Comment.dto.CommentResponseDto;
import ru.practicum.Comment.mapper.CommentMapper;
import ru.practicum.Comment.model.Comment;
import ru.practicum.Comment.model.CommentState;
import ru.practicum.Comment.repository.CommentRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.EntityNotFoundException;
import ru.practicum.exception.InvalidParameterException;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Getter
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    @Override
    public CommentResponseDto addComment(Long userId, CommentRequestDto commentRequestDto) throws InvalidParameterException, ConflictException {
        log.info("Call#CommentServiceImpl#addComment# userId: {}, commentRequestDto: {}", userId, commentRequestDto);
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new InvalidParameterException("Нет пользователя с id: " + commentRequestDto.getEventId());
        }
        Optional<Event> event = eventRepository.findById(commentRequestDto.getEventId());
        if (event.isEmpty()) {
            throw new InvalidParameterException("Нет события с id: " + commentRequestDto.getEventId());
        }
        if (!event.get().getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Можно добавить комментарий только к опубликованному событию");
        }
        Comment comment = new Comment();
        comment.setText(commentRequestDto.getText());
        comment.setUser(user.get());
        comment.setItem(event.get());
        comment.setCreated(LocalDateTime.now());
        comment.setCommentState(CommentState.WAITING);

        return CommentMapper.toCommentResponceDto(commentRepository.save(comment));
    }

    @Override
    public CommentResponseDto updateComment(Long userId, Long commentId, CommentRequestDto commentRequestDto) throws InvalidParameterException {
        log.info("Call#CommentServiceImpl#updateComment# userId: {}, commentRequestDto: {}", userId, commentRequestDto);
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new InvalidParameterException("Нет пользователя с id: " + commentRequestDto.getEventId());
        }
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
        if (user.isEmpty()) {
            throw new InvalidParameterException("Нет пользователя с id: " + userId);
        }
        Optional<Comment> comment = commentRepository.findById(commentId);
        if (comment.isEmpty()) {
            throw new InvalidParameterException("Нет комментария с id: " +commentId);
        }
        return CommentMapper.toCommentResponceDto(commentRepository.deleteCommentById(commentId));
    }

    @Override
    public CommentResponseDto getComment(Long userId, Long commentId) throws InvalidParameterException {
        log.info("Call#CommentServiceImpl#deleteComment# userId: {}, commentId: {}", userId, commentId);
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new InvalidParameterException("Нет пользователя с id: " + userId);
        }
        return CommentMapper.toCommentResponceDto(commentRepository.getById(commentId));
    }

    @Override
    public List<CommentResponseDto> getComments(Long eventId, String text, String rangeStart, String rangeEnd, String sort, Integer from, Integer size) throws InvalidParameterException {
        log.info("Call#CommentServiceImpl#getComments# userId: {}, text: {}", eventId, text);

        if (sort != null && !"ASC".equals(sort.toUpperCase()) && !"DESC".equals(sort.toUpperCase())) {
            throw new InvalidParameterException("Параметр sort может принимать или ASC или DESC");
        }
        PageRequest pageable = PageRequest.of(from / size, size);
        LocalDateTime startTime = LocalDateTime.parse(rangeStart, dateTimeFormatter);
        LocalDateTime endTime = LocalDateTime.parse(rangeEnd, dateTimeFormatter);
        return commentRepository.getComments(eventId, text, startTime, endTime, sort, CommentState.PUBLISHED, pageable).stream()
                        .map(c -> CommentMapper.toCommentResponceDto(c)).collect(Collectors.toList());
    }

    @Override
    public CommentResponseDto banComment(Long commentId) throws EntityNotFoundException, ConflictException {
        log.info("Call#CommentServiceImpl#getComments# userId: {}, text: {}", commentId);
        Optional<Comment> comment = commentRepository.findById(commentId);
        if (comment.isEmpty()) {
            throw new EntityNotFoundException("Нет комментария с id: " + commentId);
        }
        if (comment.get().getCommentState().equals(CommentState.BANNED) || comment.get().getCommentState().equals(CommentState.PUBLISHED)) {
            throw new ConflictException("Забанить комментарий можно только в состоянии WAITING");
        }
        commentRepository.updateCommentState(commentId, CommentState.BANNED);
        return CommentMapper.toCommentResponceDto(commentRepository.getById(commentId));
    }

    @Override
    public CommentResponseDto publishComment(Long commentId) throws EntityNotFoundException, ConflictException {
        log.info("Call#CommentServiceImpl#publishComment# userId: {}, text: {}", commentId);
        Optional<Comment> comment = commentRepository.findById(commentId);
        if (comment.isEmpty()) {
            throw new EntityNotFoundException("Нет комментария с id: " + commentId);
        }
        if (comment.get().getCommentState().equals(CommentState.BANNED) || comment.get().getCommentState().equals(CommentState.PUBLISHED)) {
            throw new ConflictException("Опубликовать комментарий можно только в состоянии WAITING");
        }
        commentRepository.updateCommentState(commentId, CommentState.PUBLISHED);
        return CommentMapper.toCommentResponceDto(commentRepository.getById(commentId));
    }
}
