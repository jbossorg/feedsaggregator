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
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndReplaceOptions;

public class MongoWriter implements ItemWriter {

    private Logger log = Logger.getLogger(FeedReader.class);

    @Inject
    JobContext jobContext;
    private String mongoUrl;
    private String dbName;
    private String collectionName;

    private MongoCollection<Document> collection;

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

        MongoClient mongoClient = MongoClients.create(mongoUrl);
        collection = mongoClient.getDatabase(dbName).getCollection(collectionName);
    }

    @Override
    public void close() throws Exception {

    }

    @Override
    public void writeItems(List<Object> items) throws Exception {
        final FindOneAndReplaceOptions replaceOptions = new FindOneAndReplaceOptions().upsert(true);

        for (Object item : items) {
            Document doc = (Document) item;
            Object postUrl = doc.get("url");
            log.infof("Storing blog post: %s", postUrl);
            log.tracef("Blog data: %s", doc);

            collection.findOneAndReplace(Filters.eq("url", postUrl), doc, replaceOptions);
        }
    }

    @Override
    public Serializable checkpointInfo() throws Exception {
        return null;
    }
}
