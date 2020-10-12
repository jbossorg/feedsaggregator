package org.jboss.feedsagg.dist.feeds2mongo;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.junit.Assert;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

/**
 * test for running {@link ProcessAllFeedsTest} but with no DB test. Config URL is taken from system property
 * 'configUrl'
 */
public class ConfigurationTestRun extends ProcessAllFeedsTest {

    @Override
    protected String getConfigUrl() throws Exception {
        String cPath = System.getProperty("configUrl");
        if (StringUtils.isBlank(cPath)) {
            throw new Exception("Variable `configUrl` not configured");
        }
        return cPath;
    }

    @Override
    protected void testDB() {
        MongoCollection<Document> collection = getCollection();

        Document docWithNoAuthor = collection.find(Filters.eq("author", null)).first();
        if (docWithNoAuthor != null) {
            docWithNoAuthor.put("content", "REMOVED");
            Assert.fail("The document has no author. Update RSS/ATOM Feed source or add default author to the configuration. Post: " + docWithNoAuthor.toJson());
        }
    }
}
