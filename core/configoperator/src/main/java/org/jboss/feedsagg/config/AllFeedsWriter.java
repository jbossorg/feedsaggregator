package org.jboss.feedsagg.config;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.batch.api.BatchProperty;
import javax.batch.api.chunk.ItemWriter;
import javax.batch.operations.BatchRuntimeException;
import javax.batch.operations.JobOperator;
import javax.batch.runtime.BatchRuntime;
import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.context.JobContext;
import javax.inject.Inject;

import org.jberet.runtime.JobExecutionImpl;
import org.jboss.logging.Logger;

/**
 * Writer that starts individual `process-feed.xml` job for each {@link FeedConfig}
 */
public class AllFeedsWriter implements ItemWriter {
    private Logger log = Logger.getLogger(AllFeedsConfigReader.class);

    @Inject
    JobContext jobContext;

    @Inject
    @BatchProperty(name = "PROCESS_JOB_NAME")
    String jobName = "process-feed.xml";

    @Inject
    @BatchProperty(name = "PROCESS_JOB_TIMEOUT_SEC")
    int jobTimeout = 60;

    final JobOperator jobOperator = BatchRuntime.getJobOperator();

    Properties jobProperties;

    int index;

    Set<Long> executions;

    int postsCount;
    boolean failed = false;

    @Override
    public void open(Serializable checkpoint) throws Exception {
        jobProperties = org.jboss.feedsagg.common.JobUtils.getJobParameter(jobContext);
        executions = new HashSet<>();
        postsCount = 0;
    }

    @Override
    public void writeItems(List<Object> items) throws Exception {
        for (Object item : items) {
            FeedConfig feedConfig = (FeedConfig) item;
            index++;
            Properties prop = new Properties(jobProperties);
            prop.setProperty("url", feedConfig.getUrl());
            prop.setProperty("feed", feedConfig.getCode());
            prop.setProperty("group", feedConfig.getGroup());
            if (feedConfig.getAuthor() != null) {
                prop.setProperty("author", feedConfig.getAuthor());
            }
            // Skip DB Init. Covered by parent job.
            prop.setProperty("SKIP_DB_INIT", "true");

            long executionId = jobOperator.start(jobName, prop);
            executions.add(executionId);
            log.infof("JOB_EXECUTION status=SCHEDULED. index=%s job_execution_id=%s, feed=%s", index, executionId, feedConfig);
        }
        log.infof("All jobs scheduled. Count: %s", index);

        // Wait on all executions

        for (long instanceId : executions) {
            final JobExecutionImpl exec = (JobExecutionImpl) jobOperator.getJobExecution(instanceId);

            log.debugf("Waiting for job completion jobInstance=%s timeout=%ssec", instanceId, jobTimeout);
            exec.awaitTermination(jobTimeout, TimeUnit.SECONDS);

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
