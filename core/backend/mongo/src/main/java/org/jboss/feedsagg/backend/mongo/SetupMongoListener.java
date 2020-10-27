package org.jboss.feedsagg.backend.mongo;

import java.util.Properties;

import javax.batch.api.listener.JobListener;
import javax.batch.runtime.context.JobContext;
import javax.inject.Inject;

import org.bson.Document;
import org.jboss.logging.Logger;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;

/**
 * Job listener to manage Mongo DB client and perform DB initialization. If job property `SETUP_MONGO_LISTENER_SKIP` is
 * set then no operation is performed
 */
public class SetupMongoListener implements JobListener {

    private Logger log = Logger.getLogger(SetupMongoListener.class);

    @Inject
    JobContext jobContext;

    public static final String SKIP_DB_INIT = "SKIP_DB_INIT";

    public boolean skip;

    public static final String INDEX_URL = "_url_";
    public static final String INDEX_CODE = "_code_";
    public static final String INDEX_PUBLISHED = "_published_";
    public static final String INDEX_FEED = "_feed_";
    public static final String INDEX_GROUP = "_group_";
    public static final String INDEX_TAGS = "_tags_";

    @Override
    public void beforeJob() throws Exception {
        Properties jobProperties = org.jboss.feedsagg.common.JobUtils.getJobParameter(jobContext);

        // This initialize mongoClient
        MongoClient client = FeedPostMongoWriter.getClient(jobProperties);

        skip = Boolean.parseBoolean(jobProperties.getProperty(SKIP_DB_INIT, "false"));

        if (!skip) {
            MongoCollection<Document> collection = FeedPostMongoWriter.getCollection(client, jobProperties);

            initDb(collection);
        }
    }

    protected void initDb(MongoCollection<Document> collection) {

        boolean createUrlIndex = true;
        boolean createCodeIndex = true;
        boolean createPublishedIndex = true;
        boolean createFeedIndex = true;
        boolean createGroupIndex = true;
        boolean createTagsIndex = true;
        for (Document index : collection.listIndexes()) {
            log.debugf("index: %s", index);
            String idxName = index.get("name").toString();
            switch (idxName) {
            case INDEX_URL:
                createUrlIndex = false;
                break;
            case INDEX_CODE:
                createCodeIndex = false;
                break;
            case INDEX_PUBLISHED:
                createPublishedIndex = false;
                break;
            case INDEX_FEED:
                createFeedIndex = false;
                break;
            case INDEX_GROUP:
                createGroupIndex = false;
                break;
            case INDEX_TAGS:
                createTagsIndex = false;
                break;
            }
        }
        if (createUrlIndex) {
            log.infof("Creating index %s", INDEX_URL);
            collection.createIndex(Indexes.ascending("url"), new IndexOptions().name(INDEX_URL).unique(true));
        }
        if (createCodeIndex) {
            log.infof("Creating index %s", INDEX_CODE);
            // code index is not unique because different posts from different feeds can have same code. Only URL is
            // unique
            collection.createIndex(Indexes.ascending("code"), new IndexOptions().name(INDEX_CODE).unique(false));
        }
        if (createPublishedIndex) {
            log.infof("Creating index %s", INDEX_PUBLISHED);
            collection.createIndex(Indexes.descending("published"), new IndexOptions().name(INDEX_PUBLISHED).unique(false));
        }
        if (createFeedIndex) {
            log.infof("Creating index %s", INDEX_FEED);
            collection.createIndex(Indexes.descending("feed"), new IndexOptions().name(INDEX_FEED).unique(false));
        }
        if (createGroupIndex) {
            log.infof("Creating index %s", INDEX_GROUP);
            collection.createIndex(Indexes.descending("group"), new IndexOptions().name(INDEX_GROUP).unique(false));
        }
        if (createTagsIndex) {
            log.infof("Creating index %s", INDEX_TAGS);
            collection.createIndex(Indexes.descending("tags"), new IndexOptions().name(INDEX_TAGS).unique(false));
        }
    }

    @Override
    public void afterJob() throws Exception {
        if (!skip) {
            MongoClientProvider.destroy();
        }
    }
}
