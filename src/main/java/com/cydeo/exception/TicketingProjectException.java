package com.cydeo.exception;

import io.swagger.v3.oas.annotations.Operation;

public class TicketingProjectException extends Exception{

    public TicketingProjectException(String message){
        super(message);
    }
}
