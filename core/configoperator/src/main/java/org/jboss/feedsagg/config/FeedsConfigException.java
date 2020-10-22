package org.jboss.feedsagg.config;

/**
 * Exception indicate feeds configuration problem
 */
public class FeedsConfigException extends Exception {

    public FeedsConfigException(String message, FeedConfig config) {
        super(message + ". feed_config=" + config);
    }
}
