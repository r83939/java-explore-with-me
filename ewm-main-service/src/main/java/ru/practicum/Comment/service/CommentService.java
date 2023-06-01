package ru.practicum.Comment.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.Comment.dto.CommentRequestDto;
import ru.practicum.Comment.dto.CommentResponseDto;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.EntityNotFoundException;
import ru.practicum.exception.InvalidParameterException;

import java.util.List;

public interface CommentService {
    CommentResponseDto addComment(Long userId, CommentRequestDto commentRequestDto) throws InvalidParameterException, ConflictException;

    CommentResponseDto updateComment(Long userId, Long commentId, CommentRequestDto commentRequestDto) throws InvalidParameterException;

    CommentResponseDto deleteComment(Long userId, Long commentId) throws InvalidParameterException;

    CommentResponseDto getComment(Long userId, Long commentId) throws InvalidParameterException;

    List<CommentResponseDto> getComments(Long eventId, String text, String rangeStart, String rangeEnd, String sort, Integer from, Integer size) throws InvalidParameterException;

    CommentResponseDto banComment(Long commentId) throws EntityNotFoundException, ConflictException;

    CommentResponseDto publishComment(Long commentId) throws EntityNotFoundException, ConflictException;
}
