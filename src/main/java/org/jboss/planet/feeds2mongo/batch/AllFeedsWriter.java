package org.jboss.planet.feeds2mongo.batch;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.batch.api.chunk.ItemWriter;
import javax.batch.operations.JobOperator;
import javax.batch.runtime.BatchRuntime;
import javax.batch.runtime.context.JobContext;
import javax.inject.Inject;

import org.jberet.runtime.JobExecutionImpl;
import org.jboss.logging.Logger;
import org.jboss.planet.feeds2mongo.batch.model.FeedConfig;

/**
 * Writer that starts individual `process-feed.xml` job for each {@link FeedConfig}
 */
public class AllFeedsWriter implements ItemWriter {
    private Logger log = Logger.getLogger(AllFeedsConfigReader.class);

    @Inject
    JobContext jobContext;

    final JobOperator jobOperator = BatchRuntime.getJobOperator();

    Properties jobProperties;

    int index;

    Set<Long> executions;

    @Override
    public void open(Serializable checkpoint) throws Exception {
        jobProperties = FeedReader.getJobParameter(jobContext);
        executions = new HashSet<>();
    }

    @Override
    public void writeItems(List<Object> items) throws Exception {
        for (Object item : items) {
            FeedConfig feedConfig = (FeedConfig) item;
            index++;
            log.infof("Job scheduled. index=%s, feed=%s", index, item);
            Properties prop = new Properties(jobProperties);
            prop.setProperty("url", feedConfig.getUrl());
            prop.setProperty("feed", feedConfig.getCode());
            prop.setProperty("group", feedConfig.getGroup());
            long executionId = jobOperator.start("process-feed.xml", prop);
            executions.add(executionId);
        }
        log.infof("All jobs scheduled. Count: %s", index);

        int timeout = Integer.parseInt(System.getProperty("timeout", "10"));

        // Wait on all executions
        for (long instanceId : executions) {
            final JobExecutionImpl exec = (JobExecutionImpl) jobOperator.getJobExecution(instanceId);

            log.infof("Waiting for job completion jobInstance=%s timeout=%smin", instanceId, timeout);
            exec.awaitTermination(timeout, TimeUnit.MINUTES);
            // To save memory delete execution from jberets
        }
    }

    @Override
    public void close() throws Exception {
        MongoClientProvider.destroy();
    }

    @Override
    public Serializable checkpointInfo() throws Exception {
        return index;
    }

}
