package com.shaurya.spring.timecomplexityanalyzer;

import com.shaurya.spring.timecomplexityanalyzer.dto.ComplexityResult;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ComplexityResult handleException(Exception e) {
        return new ComplexityResult("ERROR", e.getMessage());
    }
}

