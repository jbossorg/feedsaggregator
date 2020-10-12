package org.jboss.feedsagg.common;

import java.util.Properties;

import javax.batch.api.BatchProperty;
import javax.batch.api.listener.JobListener;
import javax.batch.runtime.context.JobContext;
import javax.inject.Inject;

import org.jboss.logging.Logger;

public class LoggingJobListener implements JobListener {
    protected static final Logger log = Logger.getLogger(LoggingJobListener.class);

    @Inject
    protected JobContext jobContext;

    long started, finished, durationInMs;

    @Inject
    @BatchProperty(name = "logLevel")
    String logLevelStr = "INFO";

    @Inject
    @BatchProperty(name = "logProperties")
    Boolean logProperties = true;

    Logger.Level level;

    @Override
    public void beforeJob() throws Exception {
        started = System.currentTimeMillis();
        level = Logger.Level.valueOf(logLevelStr);
        Properties properties = null;
        if (logProperties) {
            properties = org.jboss.feedsagg.common.JobUtils.getJobParameter(jobContext);
        }
        log.logf(level, "JOB_EXECUTION status=STARTED job_name=%s job_execution_id=%s properties=%s", jobContext.getJobName(), jobContext.getExecutionId(), properties);
    }

    @Override
    public void afterJob() throws Exception {
        finished = System.currentTimeMillis();
        durationInMs = finished - started;

        log.logf(level, "JOB_EXECUTION status=%s job_name=%s job_execution_id=%s job_duration_ms=%s processed_posts=%s", jobContext.getBatchStatus(), jobContext.getJobName(), jobContext.getExecutionId(), (int) durationInMs,
                jobContext.getExitStatus());
    }
}
