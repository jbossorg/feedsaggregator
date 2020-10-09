package org.jboss.planet.feeds2mongo.batch.listener;

import java.util.List;

import javax.batch.api.chunk.listener.SkipProcessListener;
import javax.batch.api.chunk.listener.SkipReadListener;
import javax.batch.api.chunk.listener.SkipWriteListener;

import org.jboss.logging.Logger;

/**
 * Logs warning if item has been skipped.
 * 
 * @see org.jboss.planet.feeds2mongo.batch.PostValidationException
 */
public class LoggingSkipListener implements SkipReadListener, SkipProcessListener, SkipWriteListener {

    protected static final Logger log = Logger.getLogger(LoggingJobListener.class);

    protected void logMessage(Exception ex) {
        log.warnf("POST_PROCESS status=SKIP reason=%s", ex.getMessage());
    }

    @Override
    public void onSkipReadItem(Exception ex) throws Exception {
        logMessage(ex);
    }

    @Override
    public void onSkipProcessItem(Object item, Exception ex) throws Exception {
        logMessage(ex);
    }

    @Override
    public void onSkipWriteItem(List<Object> items, Exception ex) throws Exception {
        logMessage(ex);
    }
}
