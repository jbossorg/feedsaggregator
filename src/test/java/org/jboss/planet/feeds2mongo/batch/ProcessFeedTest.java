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

import org.bson.Document;
import org.jberet.runtime.JobExecutionImpl;
import org.junit.Assert;
import org.junit.Test;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

/**
 * Tests of process-feed.xml
 */
public class ProcessFeedTest extends MongoBaseTest {
    private static final String jobName = "process-feed.xml";
    private static final JobOperator jobOperator = BatchRuntime.getJobOperator();

    @Test
    public void processTestFeedTest() throws Exception {
        Properties prop = getMongoWriterProperties();
        prop.setProperty("url", getAbsoluteTestFilePath("/test-feed.xml"));
        prop.setProperty("feed", "test-feed");

        final long jobExecutionId = jobOperator.start(jobName, prop);
        final JobExecutionImpl jobExecution = (JobExecutionImpl) jobOperator.getJobExecution(jobExecutionId);
        jobExecution.awaitTermination(5, TimeUnit.MINUTES);
        Assert.assertEquals(BatchStatus.COMPLETED, jobExecution.getBatchStatus());

        MongoCollection<Document> collection = getCollection();
        Assert.assertEquals(1, collection.countDocuments());

        Document post = collection.find(Filters.eq("url", "https://example.com/blog/post1/")).first();
        Assert.assertEquals("Test Title", post.get("title"));
        Assert.assertEquals("test_title", post.get("code"));
    }

    public static String getAbsoluteTestFilePath(String name) throws URISyntaxException {
        URL res = ProcessFeedTest.class.getResource(name);
        File file = Paths.get(res.toURI()).toFile();
        return "file://" + file.getAbsolutePath();
    }
}
