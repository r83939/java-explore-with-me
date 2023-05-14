package ru.practicum.exception.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.exception.*;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

@RestControllerAdvice
public class ErrorController {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorMessage> handleException(EntityNotFoundException e) {
        ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.setMessage(e.getMessage());
        errorMessage.setReason("Запрашиваемый объект не найден");
        errorMessage.setStatus("NOT_FOUND");
        Arrays.stream(e.getStackTrace())
                .map(StackTraceElement::toString)
                .forEach(errorMessage.getErrors()::add);
        errorMessage.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return new ResponseEntity<>(errorMessage, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorMessage> handleException(ConflictException e) {
        ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.setMessage(e.getMessage());
        errorMessage.setReason("Запрос не может быть выполнен из-за конфликтного обращения к ресурсу");
        errorMessage.setStatus("CONFLICT");
        Arrays.stream(e.getStackTrace())
                .map(StackTraceElement::toString)
                .forEach(errorMessage.getErrors()::add);
        errorMessage.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return new ResponseEntity<>(errorMessage, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorMessage> handleException(ForbiddenException e) {
        ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.setMessage(e.getMessage());
        errorMessage.setReason("Не выполнены условия для совершения операции");
        errorMessage.setStatus("FORBIDDEN");
        Arrays.stream(e.getStackTrace())
                .map(StackTraceElement::toString)
                .forEach(errorMessage.getErrors()::add);
        errorMessage.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return new ResponseEntity<>(errorMessage, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(InvalidParameterException.class)
    public ResponseEntity<ErrorMessage> handleException(ConstraintViolationException e) {
        ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.setMessage(e.getMessage());
        errorMessage.setReason("В запросе обнаружена ошибка");
        errorMessage.setStatus("BAD_REQUEST");
        Arrays.stream(e.getStackTrace())
                .map(StackTraceElement::toString)
                .forEach(errorMessage.getErrors()::add);
        errorMessage.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleRuntimeException(RuntimeException e) {
        ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.setMessage(e.getMessage());
        errorMessage.setReason("Внутренняя ошибка сервера");
        errorMessage.setStatus("INTERNAL_SERVER_ERROR");
        Arrays.stream(e.getStackTrace())
                .map(StackTraceElement::toString)
                .forEach(errorMessage.getErrors()::add);
        errorMessage.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    
}