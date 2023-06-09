package ru.practicum.comment.service;

import ru.practicum.comment.dto.CommentRequestDto;
import ru.practicum.comment.dto.CommentResponseDto;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.EntityNotFoundException;
import ru.practicum.exception.InvalidParameterException;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentService {
    CommentResponseDto addComment(Long userId, CommentRequestDto commentRequestDto) throws InvalidParameterException, ConflictException;

    CommentResponseDto updateComment(Long userId, Long commentId, CommentRequestDto commentRequestDto) throws InvalidParameterException;

    CommentResponseDto deleteComment(Long userId, Long commentId) throws InvalidParameterException, ConflictException;

    CommentResponseDto getComment(Long userId, Long commentId) throws InvalidParameterException;

    List<CommentResponseDto> getComments(Long eventId, String text, LocalDateTime rangeStart, LocalDateTime rangeEnd, String sort, Integer from, Integer size) throws InvalidParameterException;

    CommentResponseDto banComment(Long userId, Long commentId) throws EntityNotFoundException, ConflictException;

    CommentResponseDto publishComment(Long userId, Long commentId) throws EntityNotFoundException, ConflictException;

    List<CommentResponseDto> getCommentsByAdmin(Long userId, Long eventId, String text, LocalDateTime rangeStart, LocalDateTime rangeEnd, String sort, Integer from, Integer size) throws InvalidParameterException;
}
