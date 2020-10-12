package org.jboss.feedsagg.dist.feeds2mongo;

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
 * Tests of process-all-feeds.xml
 */
public class ProcessAllFeedsTest extends MongoBaseTest {
    private static final String jobName = "process-all-feeds.xml";
    private static final JobOperator jobOperator = BatchRuntime.getJobOperator();

    protected String getConfigUrl() throws Exception {
        return getAbsoluteTestFilePath("/test-feeds-config.yaml");
    }

    public static String getAbsoluteTestFilePath(String name) throws URISyntaxException {
        URL res = ProcessAllFeedsTest.class.getResource(name);
        File file = Paths.get(res.toURI()).toFile();
        return "file://" + file.getAbsolutePath();
    }

    @Test
    public void processTestFeedTest() throws Exception {
        Properties prop = getMongoWriterProperties();
        prop.setProperty("configUrl", getConfigUrl());

        final long jobExecutionId = jobOperator.start(jobName, prop);
        final JobExecutionImpl jobExecution = (JobExecutionImpl) jobOperator.getJobExecution(jobExecutionId);
        jobExecution.awaitTermination(10, TimeUnit.MINUTES);
        Assert.assertEquals(BatchStatus.COMPLETED, jobExecution.getBatchStatus());

        testDB();
    }

    protected void testDB() {
        MongoCollection<Document> collection = getCollection();
        Assert.assertEquals(2, collection.countDocuments());

        Document post = collection.find(Filters.eq("url", "https://example.com/blog/post1/")).first();
        Assert.assertEquals("Test Title", post.get("title"));
        Assert.assertEquals("test-group", post.get("group"));
        Assert.assertEquals("Author", post.get("author"));

        Document post2 = collection.find(Filters.eq("url", "https://example.com/blog2/post1/")).first();
        Assert.assertEquals("Test Title 2", post2.get("title"));
        Assert.assertEquals("test-group", post2.get("group"));
        Assert.assertEquals("Author from config", post2.get("author"));
    }

}
