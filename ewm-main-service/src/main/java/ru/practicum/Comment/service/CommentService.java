package ru.practicum.Comment.service;

import ru.practicum.Comment.dto.CommentRequestDto;
import ru.practicum.Comment.dto.CommentResponseDto;
import ru.practicum.Comment.model.CommentSort;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.exception.InvalidParameterException;

public interface CommentService {
    CommentResponseDto addComment(Long userId, CommentRequestDto commentRequestDto) throws InvalidParameterException;

    CommentResponseDto updateComment(Long userId, Long commentId, CommentRequestDto commentRequestDto) throws InvalidParameterException;

    CommentResponseDto deleteComment(Long userId, Long commentId) throws InvalidParameterException;

    CommentResponseDto getComment(Long userId, Long commentId);

    CommentResponseDto getComments(Long eventId, String text, String rangeStart, String rangeEnd, CommentSort sort, Integer from, Integer size);
}
