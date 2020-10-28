package org.jboss.feedsagg.dist.feeds2mongo;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.batch.operations.JobOperator;
import javax.batch.runtime.BatchRuntime;
import javax.batch.runtime.BatchStatus;

import org.bson.BsonDocument;
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
        Assert.assertEquals("test_feed-test_title", post.get("code"));
        Assert.assertEquals("Author", post.get("author"));
        Assert.assertArrayEquals(Arrays.asList("tag1", "tag2").toArray(), ((List) post.get("tags")).toArray());
    }

    @Test
    public void processTestFeedAuthorReplaceTest() throws Exception {
        Properties prop = getMongoWriterProperties();
        // by using same feed the blog is just updated
        prop.setProperty("url", getAbsoluteTestFilePath("/test-feed.xml"));
        prop.setProperty("feed", "test-feed");
        prop.setProperty("author", "Author Replace");

        final long jobExecutionId = jobOperator.start(jobName, prop);
        final JobExecutionImpl jobExecution = (JobExecutionImpl) jobOperator.getJobExecution(jobExecutionId);
        jobExecution.awaitTermination(5, TimeUnit.MINUTES);
        Assert.assertEquals(BatchStatus.COMPLETED, jobExecution.getBatchStatus());

        MongoCollection<Document> collection = getCollection();
        Assert.assertEquals(1, collection.countDocuments());

        Document post = collection.find(Filters.eq("url", "https://example.com/blog/post1/")).first();
        // author should not be replaced !!!
        Assert.assertEquals("Author", post.get("author"));
    }

    @Test
    public void processTestDuplicates() throws Exception {
        MongoCollection<Document> collection = getCollection();
        collection.deleteMany(new BsonDocument());

        Properties prop = getMongoWriterProperties();
        prop.setProperty("url", getAbsoluteTestFilePath("/test-feed-duplicates.xml"));
        prop.setProperty("feed", "test-feed");

        final long jobExecutionId = jobOperator.start(jobName, prop);
        final JobExecutionImpl jobExecution = (JobExecutionImpl) jobOperator.getJobExecution(jobExecutionId);
        jobExecution.awaitTermination(5, TimeUnit.MINUTES);
        Assert.assertEquals(BatchStatus.COMPLETED, jobExecution.getBatchStatus());

        Assert.assertEquals(2, collection.countDocuments());

        Document post1 = collection.find(Filters.eq("url", "https://example.com/blogduplicate/post1/")).first();
        Assert.assertEquals("Test Title 1", post1.get("title"));

        Document post2 = collection.find(Filters.eq("url", "https://example.com/blogduplicate/post2/")).first();
        Assert.assertEquals("Test Title 2", post2.get("title"));

        collection.deleteMany(new BsonDocument());
    }

    public static String getAbsoluteTestFilePath(String name) throws URISyntaxException {
        URL res = ProcessFeedTest.class.getResource(name);
        File file = Paths.get(res.toURI()).toFile();
        return "file://" + file.getAbsolutePath();
    }
}
