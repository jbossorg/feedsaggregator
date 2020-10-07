package org.jboss.planet.feeds2mongo.batch.listener;

import java.util.Properties;

import javax.batch.api.listener.JobListener;
import javax.batch.runtime.context.JobContext;
import javax.inject.Inject;

import org.bson.Document;
import org.jboss.logging.Logger;
import org.jboss.planet.feeds2mongo.batch.FeedMongoWriter;
import org.jboss.planet.feeds2mongo.batch.FeedReader;
import org.jboss.planet.feeds2mongo.batch.MongoClientProvider;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;

/**
 * Job listener to manage Mongo DB client and perform DB initialization. If job property `SETUP_MONGO_LISTENER_SKIP` is set then no operation is performed
 */
public class SetupMongoListener implements JobListener {

    private Logger log = Logger.getLogger(SetupMongoListener.class);

    @Inject
    JobContext jobContext;

    public static final String SKIP_DB_INIT = "SKIP_DB_INIT";

    public boolean skip;

    public static final String INDEX_URL = "_url_";

    @Override
    public void beforeJob() throws Exception {
        Properties jobProperties = FeedReader.getJobParameter(jobContext);

        // This initialize mongoClient
        MongoClient client = FeedMongoWriter.getClient(jobProperties);

        skip = Boolean.parseBoolean(jobProperties.getProperty(SKIP_DB_INIT, "false"));

        if (!skip) {
            MongoCollection<Document> collection = FeedMongoWriter.getCollection(client, jobProperties);

            initDb(collection);
        }
    }

    protected void initDb(MongoCollection<Document> collection) {

        boolean createUrlIndex = true;
        for (Document index : collection.listIndexes()) {
            if (!index.containsKey(INDEX_URL)) {
                createUrlIndex = false;
            }
        }
        if (createUrlIndex) {
            log.infof("Creating index %s", INDEX_URL);
            collection.createIndex(Indexes.ascending("url"), new IndexOptions().name(INDEX_URL).unique(true));
        }
    }

    @Override
    public void afterJob() throws Exception {
        if (!skip) {
            MongoClientProvider.destroy();
        }
    }
}
