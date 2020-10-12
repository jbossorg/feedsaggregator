package org.jboss.feedsagg.dist.feeds2mongo;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;

import org.bson.Document;
import org.jboss.logging.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;

import de.flapdoodle.embed.mongo.Command;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.config.RuntimeConfigBuilder;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.runtime.Network;

/**
 * Base test class with mongo support
 */
public class MongoBaseTest {

    private static Logger log = Logger.getLogger(MongoBaseTest.class);

    private static MongodExecutable MONGO;
    private static MongoClient mongoClient;
    private static int port = 27018;
    private static final String MONGO_URL = "mongodb://localhost:" + port;
    private static final String MONGO_DB = "test-db";
    private static final String MONGO_COLLECTION = "test-collection";

    public Properties getMongoWriterProperties() {
        Properties prop = new Properties();
        prop.setProperty("mongoUrl", MONGO_URL);
        prop.setProperty("db", MONGO_DB);
        prop.setProperty("collection", MONGO_COLLECTION);
        return prop;
    }

    public MongoCollection<Document> getCollection() {
        return mongoClient.getDatabase(MONGO_DB).getCollection(MONGO_COLLECTION);
    }

    @BeforeClass
    public static void oneTimeSetUp() {
        final java.util.logging.Logger mongoLogger = java.util.logging.Logger.getLogger(MongoBaseTest.class.getName());
        mongoLogger.setLevel(Level.WARNING);

        Version.Main version = Version.Main.V3_6;
        log.infof("Starting Mongo %s on port %s", version, port);
        try {
            IMongodConfig config = new MongodConfigBuilder().version(version).net(new Net(port, Network.localhostIsIPv6())).build();
            IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder().defaultsWithLogger(Command.MongoD, mongoLogger).build();

            MONGO = MongodStarter.getInstance(runtimeConfig).prepare(config);
            MongodProcess mongodProcess = MONGO.start();

            mongoClient = new MongoClient("localhost", port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterClass
    public static void oneTimeTearDown() {
        mongoClient.close();

        if (MONGO != null) {
            try {
                MONGO.stop();
            } catch (Exception e) {
                log.error("Unable to stop MongoDB", e);
            }
        }

    }
}
