package com.ansaf.shouldiclickthis.controller;

import com.ansaf.shouldiclickthis.exception.TooManyRequestsException;
import com.ansaf.shouldiclickthis.model.UnsuccessfulResponse;
import com.ansaf.shouldiclickthis.service.TimeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

import static org.springframework.http.HttpStatus.*;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @Autowired
    private TimeService timeService;

    @ExceptionHandler(Exception.class)
    public ResponseEntity<UnsuccessfulResponse> handleUnknownException(Exception  ex) {
        log.error(ex.getMessage());
        UnsuccessfulResponse response = UnsuccessfulResponse
                .builder()
                .message(ex.getMessage())
                .responseTime(timeService.getNowTime())
                .build();
        return new ResponseEntity<>(response, INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(TooManyRequestsException.class)
    public ResponseEntity<UnsuccessfulResponse> handleTooManyRequestsException(Exception ex){
        log.error(ex.getMessage());
        UnsuccessfulResponse response = UnsuccessfulResponse
                .builder()
                .message(ex.getMessage())
                .responseTime(timeService.getNowTime())
                .build();
        return new ResponseEntity<>(response, TOO_MANY_REQUESTS);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<UnsuccessfulResponse> invalidInput(Exception ex){
        log.error(ex.getMessage());
        UnsuccessfulResponse response = UnsuccessfulResponse
                .builder()
                .message(ex.getMessage())
                .responseTime(timeService.getNowTime())
                .build();
        return new ResponseEntity<>(response, BAD_REQUEST);
    }
}
