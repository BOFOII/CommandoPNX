package com.bofoiii.commando.exception;

public class CommandoException extends RuntimeException {
    public CommandoException(String message) {
        super(message);
    }

    public CommandoException(String message, Throwable cause) {
        super(message, cause);
    }
}
