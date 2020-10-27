package org.jboss.feedsagg.common;

import java.util.List;

import javax.batch.api.chunk.listener.RetryProcessListener;
import javax.batch.api.chunk.listener.RetryReadListener;
import javax.batch.api.chunk.listener.RetryWriteListener;

import org.jboss.logging.Logger;

/**
 * Logs warning if item needs to be retried.
 * 
 * @see RetryItemException
 */
public class LoggingRetryListener implements RetryReadListener, RetryProcessListener, RetryWriteListener {

    protected static final Logger log = Logger.getLogger(LoggingRetryListener.class);

    protected void logMessage(Exception ex) {
        log.warnf("POST_PROCESS status=RETRY reason=%s cause=%s", ex.getMessage(), ex.getCause());
    }

    @Override
    public void onRetryProcessException(Object item, Exception ex) throws Exception {
        logMessage(ex);
    }

    @Override
    public void onRetryReadException(Exception ex) throws Exception {
        logMessage(ex);
    }

    @Override
    public void onRetryWriteException(List<Object> items, Exception ex) throws Exception {
        logMessage(ex);
    }
}
