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

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndReplaceOptions;

public class FeedMongoWriter implements ItemWriter {

    private Logger log = Logger.getLogger(FeedMongoWriter.class);

    @Inject
    JobContext jobContext;

    private MongoCollection<Document> collection = null;

    private Object feed;
    private int count;

    @Override
    public void open(Serializable checkpoint) throws Exception {
        Properties jobProperties = FeedReader.getJobParameter(jobContext);
        log.debugf("Opening mongoWriter job with job properties %s", jobProperties);

        MongoClient client = FeedMongoWriter.getClient(jobProperties);
        collection = getCollection(client, jobProperties);
        count = 0;
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
        jobContext.setExitStatus("" + count);
    }

    @Override
    public void writeItems(List<Object> items) throws Exception {
        final FindOneAndReplaceOptions replaceOptions = new FindOneAndReplaceOptions().upsert(true);

        for (Object item : items) {
            Document doc = (Document) item;
            Object postUrl = doc.get("url");
            this.feed = doc.get("feed");
            log.tracef("Blog data: %s", doc);

            collection.findOneAndReplace(Filters.eq("url", postUrl), doc, replaceOptions);

            log.infof("POST_PROCESS status=STORED, url=%s", postUrl);
            count++;
        }
    }

    @Override
    public Serializable checkpointInfo() throws Exception {
        return null;
    }
}
