package org.jboss.feedsagg.common;

import java.util.Properties;

import javax.batch.runtime.BatchRuntime;
import javax.batch.runtime.JobExecution;
import javax.batch.runtime.context.JobContext;

public class JobUtils {

    public static Properties getJobParameter(JobContext jobContext) {
        long executionId = jobContext.getExecutionId();
        JobExecution jobExecution = BatchRuntime.getJobOperator().getJobExecution(executionId);
        return jobExecution.getJobParameters();
    }

}
