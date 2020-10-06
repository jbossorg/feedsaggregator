package org.jboss.planet.feeds2mongo.batch.listener;

import java.util.Properties;

import javax.batch.api.BatchProperty;
import javax.batch.api.listener.JobListener;
import javax.batch.runtime.context.JobContext;
import javax.inject.Inject;

import org.jboss.logging.Logger;
import org.jboss.planet.feeds2mongo.batch.FeedReader;

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
            properties = FeedReader.getJobParameter(jobContext);
        }
        log.logf(level, "[%s] JOB_EXECUTION status=STARTED job_name=%s job_execution_id=%s properties=%s", Thread.currentThread().getName(), jobContext.getJobName(), jobContext.getExecutionId(), properties);
    }

    @Override
    public void afterJob() throws Exception {
        finished = System.currentTimeMillis();
        durationInMs = finished - started;

        log.logf(level, "[%s] JOB_EXECUTION status=COMPLETED job_name=%s job_execution_id=%s job_duration_ms=%s processed_posts=%s", Thread.currentThread().getName(), jobContext.getJobName(), jobContext.getExecutionId(),
                (int) durationInMs, jobContext.getExitStatus());
    }
}
