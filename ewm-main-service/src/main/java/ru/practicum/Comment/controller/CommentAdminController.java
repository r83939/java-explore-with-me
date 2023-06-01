package ru.practicum.Comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.Comment.dto.CommentResponseDto;
import ru.practicum.Comment.service.CommentService;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.EntityNotFoundException;

import javax.validation.constraints.Positive;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/requests")
public class CommentAdminController {
    private final CommentService commentService;

    @PatchMapping("/{commentId}/ban")
    public CommentResponseDto banComment(@PathVariable @Positive Long commentId) throws ConflictException, EntityNotFoundException {
        log.info("Call#CommentAdminController#banComment# commentId: {}", commentId);
        return commentService.banComment(commentId);
    }

    @PatchMapping("/{commentId}/publish")
    public CommentResponseDto publishComment(@PathVariable @Positive Long commentId) throws ConflictException, EntityNotFoundException {
        log.info("Call#CommentAdminController#banComment# commentId: {}", commentId);
        return commentService.publishComment(commentId);
    }
}
