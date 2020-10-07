package org.jboss.planet.feeds2mongo.batch;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.batch.api.chunk.ItemWriter;
import javax.batch.operations.BatchRuntimeException;
import javax.batch.operations.JobOperator;
import javax.batch.runtime.BatchRuntime;
import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.context.JobContext;
import javax.inject.Inject;

import org.jberet.runtime.JobExecutionImpl;
import org.jboss.logging.Logger;
import org.jboss.planet.feeds2mongo.batch.listener.SetupMongoListener;
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

    int postsCount;
    boolean failed = false;

    @Override
    public void open(Serializable checkpoint) throws Exception {
        jobProperties = FeedReader.getJobParameter(jobContext);
        executions = new HashSet<>();
        postsCount = 0;
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
            if (feedConfig.getAuthor() != null) {
                prop.setProperty("author", feedConfig.getAuthor());
            }
            // Skip DB Init. Covered by parent job.
            prop.setProperty(SetupMongoListener.SKIP_DB_INIT, "true");
            long executionId = jobOperator.start("process-feed.xml", prop);
            executions.add(executionId);
        }
        log.infof("All jobs scheduled. Count: %s", index);

        int timeout = Integer.parseInt(System.getProperty("timeout", "10"));

        // Wait on all executions

        for (long instanceId : executions) {
            final JobExecutionImpl exec = (JobExecutionImpl) jobOperator.getJobExecution(instanceId);

            log.debugf("Waiting for job completion jobInstance=%s timeout=%smin", instanceId, timeout);
            exec.awaitTermination(timeout, TimeUnit.MINUTES);

            int count = Integer.parseInt(exec.getExitStatus());
            postsCount = postsCount + count;

            if (exec.getBatchStatus().compareTo(BatchStatus.COMPLETED) != 0) {
                failed = true;
            }
            // To save memory delete execution from jberets
        }

    }

    @Override
    public void close() throws Exception {
        jobContext.setExitStatus("" + postsCount);

        if (failed) {
            throw new BatchRuntimeException("One of job failed");
        }
    }

    @Override
    public Serializable checkpointInfo() throws Exception {
        return index;
    }

}
