package org.jboss.feedsagg.common;

/**
 * Exception to indicate retry item (post) process
 */
public class RetryItemException extends Exception {
    public RetryItemException() {
    }

    public RetryItemException(String message) {
        super(message);
    }

    public RetryItemException(String message, String postUrl) {
        this(message + " post_url=" + postUrl);
    }

    public RetryItemException(String message, Throwable cause) {
        super(message, cause);
    }

    public RetryItemException(String message, Throwable cause, Object postUrl) {
        this(message + " post_url=" + postUrl, cause);
    }

    public RetryItemException(Throwable cause) {
        super(cause);
    }

    public RetryItemException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
