package com.ansaf.shouldiclickthis.controller;

import com.ansaf.shouldiclickthis.exception.TooManyRequestsException;
import com.ansaf.shouldiclickthis.model.UnsuccessfulResponse;
import com.ansaf.shouldiclickthis.service.TimeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.client.HttpServerErrorException;

import java.time.LocalDateTime;

import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.test.util.AssertionErrors.assertEquals;


@ExtendWith(MockitoExtension.class)
public class GlobalExceptionHandlerTest {

    private Exception exception;

    @Mock
    private TimeService timeService;

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Test
    void testGenericException(){
        exception = new Exception("test exception");
        given(timeService.getNowTime()).willReturn(LocalDateTime.MAX);
        ResponseEntity<UnsuccessfulResponse> response =  globalExceptionHandler.handleUnknownException(exception);
        verifyResponse(exception, LocalDateTime.MAX, INTERNAL_SERVER_ERROR, response);
    }

    @Test
    void testTooManyRequestsException(){
        exception = new TooManyRequestsException("test exception");
        given(timeService.getNowTime()).willReturn(LocalDateTime.MAX);
        ResponseEntity<UnsuccessfulResponse> response =  globalExceptionHandler.handleTooManyRequestsException(exception);
        verifyResponse(exception, LocalDateTime.MAX, TOO_MANY_REQUESTS, response);
    }

    @Test
    void testInvalidInputException(){
        exception = new MissingServletRequestParameterException("input", "input");
        given(timeService.getNowTime()).willReturn(LocalDateTime.MAX);
        ResponseEntity<UnsuccessfulResponse> response =  globalExceptionHandler.invalidInput(exception);
        verifyResponse(exception, LocalDateTime.MAX, BAD_REQUEST, response);
    }

    private void verifyResponse(Exception e, LocalDateTime localDateTime,HttpStatus status, ResponseEntity<UnsuccessfulResponse> response){
        assertEquals("Error code is wrong", status, response.getStatusCode());
        assertEquals("Response time is wrong", localDateTime, response.getBody().getResponseTime());
        assertEquals("Response Message is wrong", e.getMessage(), response.getBody().getMessage());
    }
}
