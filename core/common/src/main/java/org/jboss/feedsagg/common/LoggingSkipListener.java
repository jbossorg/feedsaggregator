package org.jboss.feedsagg.common;

import javax.batch.api.chunk.listener.SkipProcessListener;
import javax.batch.api.chunk.listener.SkipReadListener;

import org.jboss.logging.Logger;

/**
 * Logs warning if item has been skipped. Intentionally it doesn't implement SkipWriterListener because it skip whole
 * batch not only one item.
 * 
 * @see SkipItemException
 */
public class LoggingSkipListener implements SkipReadListener, SkipProcessListener {

    protected static final Logger log = Logger.getLogger(LoggingSkipListener.class);

    public static void logMessage(Exception ex) {
        log.warnf("POST_PROCESS status=SKIP reason=%s cause=%s", ex.getMessage(), ex.getCause());
    }

    @Override
    public void onSkipReadItem(Exception ex) throws Exception {
        logMessage(ex);
    }

    @Override
    public void onSkipProcessItem(Object item, Exception ex) throws Exception {
        logMessage(ex);
    }

}
