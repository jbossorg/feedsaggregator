package org.jboss.planet.feeds2mongo.batch;

import java.io.Serializable;
import java.util.List;

import javax.batch.api.chunk.ItemWriter;

import org.jboss.logging.Logger;

public class LoggingWriter implements ItemWriter {

    private Logger log = Logger.getLogger(LoggingWriter.class);


    public void open(Serializable serializable) {

    }

    public void close() {

    }

    public void writeItems(List<Object> items) throws Exception {
        for (final Object e : items) {
            log.infof("Feed Entry: %s", e);
        }
    }

    public Serializable checkpointInfo() {
        return null;
    }
}
