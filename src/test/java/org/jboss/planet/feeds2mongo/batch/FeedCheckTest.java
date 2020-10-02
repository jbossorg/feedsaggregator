package org.jboss.planet.feeds2mongo.batch;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.batch.operations.JobOperator;
import javax.batch.runtime.BatchRuntime;
import javax.batch.runtime.BatchStatus;

import org.jberet.runtime.JobExecutionImpl;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests of feed-check.xml
 */
public class FeedCheckTest {
    private static final String jobName = "feed-check.xml";
    private static final JobOperator jobOperator = BatchRuntime.getJobOperator();

    @Test
    public void feedCheckToConsole() throws Exception {
        Properties prop = new Properties();
        prop.setProperty("url", getAbsoluteTestFilePath("/test-feed.xml"));
        final long jobExecutionId = jobOperator.start(jobName, prop);
        final JobExecutionImpl jobExecution = (JobExecutionImpl) jobOperator.getJobExecution(jobExecutionId);
        jobExecution.awaitTermination(5, TimeUnit.MINUTES);
        Assert.assertEquals(BatchStatus.COMPLETED, jobExecution.getBatchStatus());
    }

    public static String getAbsoluteTestFilePath(String name) throws URISyntaxException {
        URL res = FeedCheckTest.class.getResource(name);
        File file = Paths.get(res.toURI()).toFile();
        return "file://" + file.getAbsolutePath();
    }
}
