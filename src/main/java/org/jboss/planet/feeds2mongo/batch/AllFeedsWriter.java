package org.jboss.planet.feeds2mongo.batch;

import java.io.Serializable;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.batch.api.chunk.ItemWriter;
import javax.batch.operations.JobOperator;
import javax.batch.runtime.BatchRuntime;
import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.JobInstance;
import javax.batch.runtime.context.JobContext;
import javax.inject.Inject;

import org.jberet.runtime.JobExecutionImpl;
import org.jboss.logging.Logger;
import org.jboss.planet.feeds2mongo.batch.model.FeedConfig;

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
            FeedConfig feedConfig = (FeedConfig) item;
            index++;
            log.infof("Job scheduled. index=%s, feed=%s", index, item);
            Properties prop = new Properties(jobProperties);
            prop.setProperty("url", feedConfig.getUrl());
            prop.setProperty("feed", feedConfig.getCode());
            prop.setProperty("group", feedConfig.getGroup());
            jobOperator.start("process-feed.xml", prop);
        }
    }

    @Override
    public void close() throws Exception {
        log.infof("All jobs scheduled. Count: %s", index);

        int timeout = Integer.parseInt(System.getProperty("timeout", "10"));
        // Wait on all executions
        JobOperator jobOperator = BatchRuntime.getJobOperator();
        List<JobInstance> jobInstances = jobOperator.getJobInstances("process-feed", 0, Integer.MAX_VALUE);
        for (JobInstance instance : jobInstances) {
            final JobExecutionImpl exec = (JobExecutionImpl) jobOperator.getJobExecution(instance.getInstanceId());

            if (exec.getBatchStatus().compareTo(BatchStatus.COMPLETED) != 0) {
                log.infof("Waiting for job completion jobInstance=%s timeout=%s", instance.getInstanceId(), timeout);
                exec.awaitTermination(timeout, TimeUnit.MINUTES);
            }
        }
    }

    @Override
    public Serializable checkpointInfo() throws Exception {
        return null;
    }

}
