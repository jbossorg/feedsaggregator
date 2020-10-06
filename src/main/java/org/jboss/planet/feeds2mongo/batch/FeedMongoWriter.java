package org.jboss.planet.feeds2mongo.batch;

import java.io.Serializable;
import java.util.List;
import java.util.Properties;

import javax.batch.api.chunk.ItemWriter;
import javax.batch.operations.BatchRuntimeException;
import javax.batch.runtime.context.JobContext;
import javax.inject.Inject;

import org.bson.Document;
import org.jboss.logging.Logger;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndReplaceOptions;

public class FeedMongoWriter implements ItemWriter {

    private Logger log = Logger.getLogger(FeedReader.class);

    @Inject
    JobContext jobContext;
    private String mongoUrl;
    private String dbName;
    private String collectionName;

    MongoClientProvider mongoClientProvider;

    private static MongoCollection<Document> collection = null;

    private Object feed;
    private int count;

    @Override
    public void open(Serializable checkpoint) throws Exception {
        Properties jobProperties = FeedReader.getJobParameter(jobContext);

        log.infof("Opening mongoWriter job with job properties %s", jobProperties);

        mongoUrl = jobProperties.getProperty("mongoUrl");
        if (mongoUrl == null) {
            throw new BatchRuntimeException("job parameter `mongoUrl` must be defined");
        }
        dbName = jobProperties.getProperty("db");
        if (dbName == null) {
            throw new BatchRuntimeException("job parameter `db` must be defined");
        }
        collectionName = jobProperties.getProperty("collection");
        if (collectionName == null) {
            throw new BatchRuntimeException("job parameter `collection` must be defined");
        }
        collection = mongoClientProvider.getClient(mongoUrl).getDatabase(dbName).getCollection(collectionName);
        count = 0;
    }

    @Override
    public void close() throws Exception {
        log.infof("[%s] INDEX_PROCESS status=COMPLETED feed=%s count=%s", Thread.currentThread().getName(),  feed, count);
    }

    @Override
    public void writeItems(List<Object> items) throws Exception {
        final FindOneAndReplaceOptions replaceOptions = new FindOneAndReplaceOptions().upsert(true);

        for (Object item : items) {
            Document doc = (Document) item;
            Object postUrl = doc.get("url");
            this.feed = doc.get("feed");
            log.infof("[%s] Storing blog post: %s", Thread.currentThread().getName(), postUrl);
            log.tracef("Blog data: %s", doc);

            collection.findOneAndReplace(Filters.eq("url", postUrl), doc, replaceOptions);

            count++;
        }
    }

    @Override
    public Serializable checkpointInfo() throws Exception {
        return null;
    }
}
