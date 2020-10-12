package org.jboss.feedsagg.backend.mongo;

import org.jboss.logging.Logger;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

public class MongoClientProvider {

    private static Logger log = Logger.getLogger(MongoClientProvider.class);

    private static MongoClient mongoClient;

    public static synchronized MongoClient getClient(String mongoUrl) {
        if (mongoClient == null) {
            log.info("MONGO_CLIENT status=CREATED");
            mongoClient = MongoClients.create(mongoUrl);
        }
        return mongoClient;
    }

    public static synchronized void destroy() {
        if (mongoClient != null) {
            log.info("MONGO_CLIENT status=CLOSED");
            mongoClient.close();
            mongoClient = null;
        }
    }

}
