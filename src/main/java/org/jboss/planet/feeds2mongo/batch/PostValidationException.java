package org.jboss.planet.feeds2mongo.batch;

/**
 * Exception to indicate post in feed is invalid
 */
public class PostValidationException extends Exception {
    public PostValidationException() {
    }

    public PostValidationException(String message) {
        super(message);
    }

    public PostValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public PostValidationException(Throwable cause) {
        super(cause);
    }

    public PostValidationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
