package ru.practicum.exception.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.exception.ErrorMessage;
import ru.practicum.exception.InvalidParameterException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@RestControllerAdvice
public class ErrorController {

    @ExceptionHandler(InvalidParameterException.class)
    public ResponseEntity<ErrorMessage> handleException(InvalidParameterException e) {
        ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.setMessage(e.getMessage());
        errorMessage.setReason("В запросе обнаружена ошибка");
        errorMessage.setStatus("BAD_REQUEST");
        errorMessage.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }
}
