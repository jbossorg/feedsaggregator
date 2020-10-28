package org.jboss.feedsagg.backend.mongo;

import java.io.Serializable;
import java.util.List;
import java.util.Properties;

import javax.batch.api.chunk.ItemWriter;
import javax.batch.operations.BatchRuntimeException;
import javax.batch.runtime.context.JobContext;
import javax.inject.Inject;

import org.bson.Document;
import org.jboss.feedsagg.common.LoggingSkipListener;
import org.jboss.feedsagg.common.RetryItemException;
import org.jboss.feedsagg.common.SkipItemException;
import org.jboss.logging.Logger;

import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndReplaceOptions;

/**
 * Stores individual {@link Document} to Mongo
 */
public class FeedPostMongoWriter implements ItemWriter {

    private Logger log = Logger.getLogger(FeedPostMongoWriter.class);

    @Inject
    JobContext jobContext;

    private MongoCollection<Document> collection = null;

    private Object feed;
    private int rowNumber;

    @Override
    public void open(Serializable checkpoint) throws Exception {
        Properties jobProperties = org.jboss.feedsagg.common.JobUtils.getJobParameter(jobContext);
        log.debugf("Opening mongoWriter job with job properties %s", jobProperties);

        MongoClient client = FeedPostMongoWriter.getClient(jobProperties);
        collection = getCollection(client, jobProperties);

        if (checkpoint != null) {
            rowNumber = (Integer) checkpoint;
        } else {
            rowNumber = 0;
        }
    }

    public static MongoClient getClient(Properties jobProperties) {
        String mongoUrl = jobProperties.getProperty("mongoUrl");
        if (mongoUrl == null) {
            throw new BatchRuntimeException("job parameter `mongoUrl` must be defined");
        }
        return MongoClientProvider.getClient(mongoUrl);
    }

    public static MongoCollection<Document> getCollection(MongoClient client, Properties jobProperties) {
        String dbName = jobProperties.getProperty("db");
        if (dbName == null) {
            throw new BatchRuntimeException("job parameter `db` must be defined");
        }
        String collectionName = jobProperties.getProperty("collection");
        if (collectionName == null) {
            throw new BatchRuntimeException("job parameter `collection` must be defined");
        }
        return client.getDatabase(dbName).getCollection(collectionName);
    }

    @Override
    public void close() throws Exception {
        jobContext.setExitStatus("" + rowNumber);
    }

    @Override
    public void writeItems(List<Object> items) throws Exception {
        final FindOneAndReplaceOptions replaceOptions = new FindOneAndReplaceOptions().upsert(true);

        for (; rowNumber < items.size(); rowNumber++) {
            Document doc = (Document) items.get(rowNumber);
            Object postUrl = doc.get("url");
            this.feed = doc.get("feed");
            log.tracef("Blog data: %s", doc);

            try {
                collection.findOneAndReplace(Filters.eq("url", postUrl), doc, replaceOptions);
            } catch (MongoException e) {
                // Skip duplicities. Exception like:
                // Command failed with error 11000 (DuplicateKey): 'E11000 duplicate key error collection:
                // test-db.test-collection index: _code_ dup key: { : "test_title_1" }'
                if (e.getCode() == 11000) {
                    // Do not throw SkipItemException because it stop whole batch !!!
                    // Just continue to next item.
                    LoggingSkipListener.logMessage(new SkipItemException("blog post with code already exists.", e, postUrl));
                    continue;
                }

                throw new RetryItemException("Cannot store blogpost", e, postUrl);
            }

            log.infof("POST_PROCESS status=STORED, url=%s", postUrl);
        }
    }

    @Override
    public Serializable checkpointInfo() throws Exception {
        return rowNumber;
    }
}
