package org.jboss.planet.feeds2mongo.batch;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.batch.api.chunk.ItemWriter;
import javax.batch.operations.JobOperator;
import javax.batch.runtime.BatchRuntime;
import javax.batch.runtime.context.JobContext;
import javax.inject.Inject;

import org.jboss.logging.Logger;

public class AllFeedsWriter implements ItemWriter {
    private Logger log = Logger.getLogger(AllFeedsConfigReader.class);

    @Inject
    JobContext jobContext;

    final JobOperator jobOperator = BatchRuntime.getJobOperator();

    Properties jobProperties;

    int index;

    @Override
    public void open(Serializable checkpoint) throws Exception {
        jobProperties = FeedReader.getJobParameter(jobContext);
    }

    @Override
    public void writeItems(List<Object> items) throws Exception {
        for (Object item : items) {
            Map<String, Object> feedConfig = (Map<String, Object>) item;
            index++;
            log.infof("Job scheduled. index=[%s], feed=%s", index, item);
            Properties prop = new Properties(jobProperties);
            prop.setProperty("url", feedConfig.get("url").toString());
            prop.setProperty("feed", feedConfig.get("code").toString());
            jobOperator.start("process-feed.xml", prop);
        }
    }

    @Override
    public void close() throws Exception {
        log.infof("All jobs scheduled. Count: %s", index);
    }

    @Override
    public Serializable checkpointInfo() throws Exception {
        return null;
    }

}
