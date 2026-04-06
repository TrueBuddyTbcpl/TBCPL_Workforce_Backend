// com.tbcpl.workforce.common.exception.EmailDeliveryException.java
package com.tbcpl.workforce.common.exception;

public class EmailDeliveryException extends RuntimeException {

    public EmailDeliveryException(String message) {
        super(message);
    }

    public EmailDeliveryException(String message, Throwable cause) {
        super(message, cause);
    }
}