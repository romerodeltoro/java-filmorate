package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.ResponseError;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<ResponseError>> notValidException(MethodArgumentNotValidException e) {
        log.error(e.getMessage());
        final List<ResponseError> violations = e.getBindingResult().getFieldErrors().stream()
                .map(error -> new ResponseError(error.getDefaultMessage()))
                .collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(violations);
    }

    @ExceptionHandler(FilmNotFoundException.class)
    public ResponseEntity<ResponseError> filmNotFoundException(FilmNotFoundException e) {
        log.error(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ResponseError(e.getMessage()));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ResponseError> userNotFoundException(UserNotFoundException e) {
        log.error(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ResponseError(e.getMessage()));
    }

    @ExceptionHandler(MpaNotFoundException.class)
    public ResponseEntity<ResponseError> mpaNotFoundException(MpaNotFoundException e) {
        log.error(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ResponseError(e.getMessage()));
    }

    @ExceptionHandler(GenreNotFoundException.class)
    public ResponseEntity<ResponseError> genreNotFoundException(GenreNotFoundException e) {
        log.error(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ResponseError(e.getMessage()));
    }

    @ExceptionHandler({NotWritableException.class, UserAlreadyExistException.class})
    public ResponseEntity<ResponseError> notWritableException(RuntimeException e) {
        log.error(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResponseError(e.getMessage()));
    }

}
