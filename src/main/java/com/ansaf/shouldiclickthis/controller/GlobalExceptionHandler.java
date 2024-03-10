package com.ansaf.shouldiclickthis.controller;

import com.ansaf.shouldiclickthis.model.UnsuccessfulResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<UnsuccessfulResponse> handleUnknownException(Exception  ex) {
        log.error(ex.getMessage());
        UnsuccessfulResponse response = UnsuccessfulResponse
                .builder()
                .message(ex.getMessage())
                .responseTime(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(response, INTERNAL_SERVER_ERROR);
    }
}
