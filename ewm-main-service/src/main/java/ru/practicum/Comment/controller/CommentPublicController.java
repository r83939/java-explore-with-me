package ru.practicum.Comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.Comment.dto.CommentResponseDto;
import ru.practicum.Comment.model.CommentSort;
import ru.practicum.Comment.service.CommentService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/events/{eventId}/comments/")
public class CommentPublicController {
    private final CommentService commentService;

    @GetMapping()
    public CommentResponseDto getComments(@PathVariable @Positive Long eventId,
                                          @RequestParam(required = false) String text,
                                          @RequestParam(required = false) String rangeStart,
                                          @RequestParam(required = false) String rangeEnd,
                                          @RequestParam(required = false) CommentSort sort,
                                          @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                          @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Call#CommentUserController#getComment#  eventId: {}");
        return commentService.getComments(eventId,text, rangeStart, rangeEnd, sort, from, size);
    }

}
