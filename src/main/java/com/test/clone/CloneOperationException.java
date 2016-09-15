package com.test.clone;

public class CloneOperationException extends RuntimeException {
    public CloneOperationException() {
    }

    public CloneOperationException(String message) {
        super(message);
    }

    public CloneOperationException(String message, Throwable cause) {
        super(message, cause);
    }

    public CloneOperationException(Throwable cause) {
        super(cause);
    }

    public CloneOperationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
