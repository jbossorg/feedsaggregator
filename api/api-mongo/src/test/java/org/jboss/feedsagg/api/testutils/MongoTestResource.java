package org.jboss.feedsagg.api.testutils;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.bson.Document;
import org.jboss.feedsagg.api.model.BlogPost;
import org.jboss.feedsagg.backend.mongo.MongoClientProvider;
import org.jboss.feedsagg.backend.mongo.SetupMongoListener;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.Assertions;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;

import de.flapdoodle.embed.mongo.Command;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongoCmdOptionsBuilder;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.config.RuntimeConfigBuilder;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.runtime.Network;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

/**
 * Initialization of the InMemory MongoDB instance for tests. Imports data from src/test/resources/mongoData.txt file
 * which contains document per line.
 *
 * @author Vlastimil Elias (velias at redhat dot com)
 */
public class MongoTestResource implements QuarkusTestResourceLifecycleManager {

    private static Logger log = Logger.getLogger(MongoTestResource.class);

    private static final String HOST = "localhost";
    protected static final int PORT = 27018;

    // TODO how to get Mongo DB name and collection name from the application.properties?
    protected static final String dbName = "feeds2mongo";
    protected static final String collectionName = "posts";

    private MongodExecutable MONGO;

    private static Map<String, Document> importedDocuments = new HashMap<>();

    /**
     * Get documents imported into mongo so we can use them for assertions on API responses.
     * 
     * @return map of documents, where key is "code" taken from the document
     */
    public static Map<String, Document> getImportedDocuments() {
        return importedDocuments;
    }

    @Override
    public void init(Map<String, String> initArgs) {
        log.infof("Init: " + initArgs);
    }

    @Override
    public Map<String, String> start() {
        Version.Main version = Version.Main.V3_6;
        log.infof("Starting MongoDB version %s on localhost port %s", version, PORT);
        try {
            IMongodConfig config = new MongodConfigBuilder().version(version).net(new Net(HOST, PORT, Network.localhostIsIPv6())).cmdOptions(new MongoCmdOptionsBuilder().useNoJournal(false).build()).build();

            @SuppressWarnings("deprecation")
            IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder().defaultsWithLogger(Command.MongoD, java.util.logging.Logger.getLogger(getClass().getName())).build();

            MONGO = MongodStarter.getInstance(runtimeConfig).prepare(config);
            MONGO.start();
            log.infof("MongoDB started, going to init collection " + collectionName + " in the database " + dbName);

            try (MongoClient client = MongoClientProvider.getClient("mongodb://" + HOST + ":" + PORT)) {
                MongoCollection<Document> collection = client.getDatabase(dbName).getCollection(collectionName);
                SetupMongoListener.initDb(collection);

                importDocumentsFromJsonFile(collection, "/mongoTestData.txt");
            }
            log.infof("MongoDB initialized!");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return Collections.emptyMap();

    }

    protected void importDocumentsFromJsonFile(MongoCollection<Document> collection, String resourceFileName) {
        log.infof("Going to import data into MongoDB from resource file " + resourceFileName);
        // Read each line of the json file. Each file is one observation document.
        try (BufferedReader br = new BufferedReader(new java.io.InputStreamReader(MongoTestResource.class.getResourceAsStream(resourceFileName), Charset.defaultCharset()));) {
            String line;
            while ((line = br.readLine()) != null) {
                Document d = Document.parse(line);
                importedDocuments.put(d.getString("code"), d);
            }
        } catch (IOException ex) {
            throw new RuntimeException("Failed to load data for import");
        }
        collection.insertMany(new ArrayList<>(importedDocuments.values()));
        log.infof("Imported " + importedDocuments.size() + " documents.");
    }

    /**
     * Verifies REST API response blogpost POJO against document loaded into mongodb
     * 
     * @param code of the blogpost
     * @param response blogpost object
     * @param contentPresent if true content is verified for match, if false content in response have to be null
     */
    public static void verifyResponseBlogPost(String code, BlogPost response, boolean contentPresent) {

        Document mongoDoc = MongoTestResource.getImportedDocuments().get(code);
        Assertions.assertNotNull(mongoDoc, "Document not found to be imported into Mongo for code " + code);

        // id is not published over the API!
        Assertions.assertNull(response.getId());
        Assertions.assertEquals(code, response.getCode());
        // next code also verifies that translation from mongo document to the REST API alue object works correctly!
        Assertions.assertEquals(mongoDoc.getString("title"), response.getTitle());
        Assertions.assertEquals(mongoDoc.getString("url"), response.getUrl());
        Assertions.assertEquals(mongoDoc.getString("feed"), response.getFeed());
        Assertions.assertEquals(mongoDoc.getString("group"), response.getGroup());
        Assertions.assertEquals(mongoDoc.getString("author"), response.getAuthor());
        if (contentPresent) {
            Assertions.assertEquals(mongoDoc.getString("content"), response.getContent());
        } else {
            Assertions.assertNull(response.getContent());
        }
        Assertions.assertEquals(mongoDoc.getString("contentPreview"), response.getContentPreview());
        Assertions.assertEquals(mongoDoc.getDate("published"), response.getPublished());
        Assertions.assertEquals(mongoDoc.getDate("updated"), response.getUpdated());
        Assertions.assertEquals(mongoDoc.getList("tags", String.class), response.getTags());
    }

    @Override
    public void stop() {
        if (MONGO != null) {
            try {
                MONGO.stop();
            } catch (Exception e) {
                log.error("Unable to stop MongoDB", e);
            }
        }
    }
}
