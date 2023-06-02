package ru.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentResponseDto;
import ru.practicum.comment.service.CommentService;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.EntityNotFoundException;
import ru.practicum.exception.InvalidParameterException;

import javax.validation.constraints.Positive;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/{userId}/comments")
public class CommentAdminController {
    private final CommentService commentService;

    @PatchMapping("/{commentId}/ban")
    public CommentResponseDto banComment(@PathVariable @Positive Long userId,
                                         @PathVariable @Positive Long commentId) throws ConflictException, EntityNotFoundException {
        log.info("Call#CommentAdminController#banComment# commentId: {}", commentId);
        return commentService.banComment(userId, commentId);
    }

    @PatchMapping("/{commentId}/publish")
    public CommentResponseDto publishComment(@PathVariable @Positive Long userId,
                                             @PathVariable @Positive Long commentId) throws ConflictException, EntityNotFoundException {
        log.info("Call#CommentAdminController#banComment# commentId: {}", commentId);
        return commentService.publishComment(userId, commentId);
    }

    @GetMapping("/{commentId}")
    public CommentResponseDto getComment(@PathVariable @Positive Long userId,
                                         @PathVariable @Positive Long commentId) throws InvalidParameterException {
        log.info("Call#CommentUserController#getComment# userId: {},  commentId: {}", userId, commentId);
        return commentService.getComment(userId, commentId);
    }
}
