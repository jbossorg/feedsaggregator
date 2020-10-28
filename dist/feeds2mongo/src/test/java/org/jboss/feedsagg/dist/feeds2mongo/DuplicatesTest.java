package org.jboss.feedsagg.dist.feeds2mongo;

import org.bson.Document;
import org.junit.Assert;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

/**
 * test for running test-feeds-config-duplicates.yaml
 */
public class DuplicatesTest extends ProcessAllFeedsTest {

    @Override
    protected String getConfigUrl() throws Exception {
        return getAbsoluteTestFilePath("/test-feeds-config-duplicates.yaml");
    }

    @Override
    protected void testDB() {
        MongoCollection<Document> collection = getCollection();
        // 4 blog posts should be stored. The
        Assert.assertEquals(4, collection.countDocuments());

        // Two blog posts from different feeds with same title
        Document post = collection.find(Filters.eq("url", "https://example.com/blog2/post1/")).first();
        Assert.assertEquals("test2-test_title_2", post.get("code"));
        Assert.assertEquals("Test Title 2", post.get("title"));

        Document post2 = collection.find(Filters.eq("url", "https://example.com/blogduplicate/post2/")).first();
        Assert.assertEquals("test_duplicates-test_title_2", post2.get("code"));
        Assert.assertEquals("Test Title 2", post2.get("title"));
    }
}
