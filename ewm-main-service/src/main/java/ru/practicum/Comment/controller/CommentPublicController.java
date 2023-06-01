package ru.practicum.Comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.Comment.dto.CommentResponseDto;
import ru.practicum.Comment.service.CommentService;
import ru.practicum.exception.InvalidParameterException;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/events/{eventId}/comments/")
public class CommentPublicController {
    private final CommentService commentService;

    @GetMapping()
    public List<CommentResponseDto> getComments(@PathVariable @Positive Long eventId,
                                                @RequestParam(required = false) String text,
                                                @RequestParam(required = false) String rangeStart,
                                                @RequestParam(required = false) String rangeEnd,
                                                @RequestParam(required = false) String sort,
                                                @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                @Positive @RequestParam(defaultValue = "10") Integer size) throws InvalidParameterException {
        log.info("Call#CommentUserController#getComment#  eventId: {}");
        return commentService.getComments(eventId, text, rangeStart, rangeEnd, sort, from, size);
    }

}
