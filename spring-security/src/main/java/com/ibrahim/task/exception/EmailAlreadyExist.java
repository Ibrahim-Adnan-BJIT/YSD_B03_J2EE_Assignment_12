package com.ibrahim.task.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class EmailAlreadyExist extends RuntimeException{
    private String message;
    public EmailAlreadyExist( String message1) {
        super(message1);
    }
}
