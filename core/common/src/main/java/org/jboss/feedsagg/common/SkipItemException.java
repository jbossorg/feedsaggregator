package org.jboss.feedsagg.common;

/**
 * Exception to indicate item (post) should be skipped
 */
public class SkipItemException extends Exception {
    public SkipItemException() {
    }

    public SkipItemException(String message) {
        super(message);
    }

    public SkipItemException(String message, String postUrl) {
        this(message + " post_url=" + postUrl);
    }

    public SkipItemException(String message, Throwable cause) {
        super(message, cause);
    }

    public SkipItemException(String message, Throwable cause, Object postUrl) {
        this(message + " post_url=" + postUrl, cause);
    }

    public SkipItemException(Throwable cause) {
        super(cause);
    }

    public SkipItemException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
