package ru.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentResponseDto;
import ru.practicum.comment.service.CommentService;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.EntityNotFoundException;
import ru.practicum.exception.InvalidParameterException;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/{adminId}/comments")
public class CommentAdminController {
    private final CommentService commentService;

    @PatchMapping("/{commentId}/ban")
    public CommentResponseDto banComment(@PathVariable @Positive Long adminId,
                                         @PathVariable @Positive Long commentId) throws ConflictException, EntityNotFoundException {
        log.info("Call#CommentAdminController#banComment# commentId: {}", commentId);
        return commentService.banComment(adminId, commentId);
    }

    @PatchMapping("/{commentId}/publish")
    public CommentResponseDto publishComment(@PathVariable @Positive Long adminId,
                                             @PathVariable @Positive Long commentId) throws ConflictException, EntityNotFoundException {
        log.info("Call#CommentAdminController#banComment# commentId: {}", commentId);
        return commentService.publishComment(adminId, commentId);
    }

    @GetMapping("/{commentId}")
    public CommentResponseDto getComment(@PathVariable @Positive Long adminId,
                                         @PathVariable @Positive Long commentId) throws InvalidParameterException {
        log.info("Call#CommentUserController#getComment# userId: {},  commentId: {}", adminId, commentId);
        return commentService.getComment(adminId, commentId);
    }

    @GetMapping("/user/{userId}")
    public List<CommentResponseDto> getComments(@PathVariable @Positive Long adminId,
                                                @PathVariable @Positive Long userId,
                                                @RequestParam(required = false) Long eventId,
                                                @RequestParam(required = false) String text,
                                                @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                                @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                                @RequestParam(required = false) String sort,
                                                @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                @Positive @RequestParam(defaultValue = "10") Integer size) throws InvalidParameterException {
        log.info("Call#CommentUserController#getComment# adminId: {}, userId: {}", adminId, userId);
        return commentService.getCommentsByAdmin(userId, eventId, text, rangeStart, rangeEnd, sort, from, size);
    }
}
