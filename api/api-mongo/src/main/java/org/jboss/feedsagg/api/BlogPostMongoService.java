package org.jboss.feedsagg.api;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.feedsagg.api.model.BlogPost;
import org.jboss.logging.Logger;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;

import io.quarkus.cache.CacheResult;
import io.quarkus.mongodb.FindOptions;
import io.quarkus.mongodb.reactive.ReactiveMongoClient;
import io.quarkus.mongodb.reactive.ReactiveMongoCollection;
import io.smallrye.mutiny.Uni;

@ApplicationScoped
public class BlogPostMongoService {

    private final Logger log = Logger.getLogger(BlogPostMongoService.class);

    @Inject
    ReactiveMongoClient mongoClient;

    @ConfigProperty(name = "app.mongo.db")
    String db;

    @ConfigProperty(name = "app.mongo.collection")
    String collection;

    public static BlogPost convertDocument(Document doc) {
        BlogPost p = new BlogPost(doc.getObjectId("_id").toString());
        p.setCode(doc.getString("code"));
        p.setUrl(doc.getString("url"));
        p.setFeed(doc.getString("feed"));
        p.setGroup(doc.getString("group"));
        p.setTitle(doc.getString("title"));
        p.setAuthor(doc.getString("author"));

        p.setPublished(doc.getDate("published"));
        p.setUpdated(doc.getDate("updated"));
        p.setContent(doc.getString("content"));
        p.setContentPreview(doc.getString("contentPreview"));
        p.setTags(doc.getList("tags", String.class));

        return p;
    }

    @CacheResult(cacheName = "search")
    public Uni<List<BlogPost>> search(Integer from, Integer size, String sort, List<String> feeds, List<String> groups, List<String> tags) {
        Set<Bson> filters = new HashSet<>();
        addFilterByListOfValues(filters, "feed", feeds);
        addFilterByListOfValues(filters, "group", groups);
        addFilterByListOfValues(filters, "tags", tags);

        Bson filter = new Document();
        if (filters.size() > 0) {
            filter = Filters.and(filters);
        }

        FindOptions options = new FindOptions();
        if (size == null || size < 1) {
            size = 10;
        }
        if (size > 100) {
            size = 100;
        }
        options.limit(size);
        if (from != null) {
            options.skip(from);
        }
        if (StringUtils.equalsIgnoreCase(sort, "asc")) {
            options.sort(Sorts.ascending("published"));
        } else {
            options.sort(Sorts.descending("published"));
        }

        log.debugf("Search, filter=%s, options=%s", filter, options);

        return getCollection().find(filter, options).map(BlogPostMongoService::convertDocument).collectItems().asList();
    }

    /**
     * Add filter to select documents containing at least one value from the list of values in the defined field. Values
     * from the list are sanitized:
     * <ul>
     * <li>if list is null or empty then no filter is added
     * <li>blank value from the list is ignored
     * </ul>
     * 
     * @param filters to add additional condition to - it is expected this is AND filter!
     * @param fieldName in the document to filter over
     * @param values to filter by
     */
    protected void addFilterByListOfValues(Set<Bson> filters, String fieldName, List<String> values) {
        if (values != null && values.size() > 0) {
            Set<Bson> orFilters = new HashSet<>();
            for (String value : values) {
                if (StringUtils.isNotBlank(value))
                    orFilters.add(Filters.eq(fieldName, value));
            }
            if (orFilters.size() > 0) {
                filters.add(Filters.or(orFilters));
            }
        }
    }

    @CacheResult(cacheName = "postcache")
    public Uni<BlogPost> getPostById(String id) {
        log.debugf("Get Post, id=%s", id);

        try {
            ObjectId objectId = new ObjectId(id);

            return getCollection().find(Filters.eq("_id", objectId)).map(BlogPostMongoService::convertDocument).collectItems().first();
        } catch (IllegalArgumentException e) {
            return Uni.createFrom().failure(e);
        }
    }

    @CacheResult(cacheName = "postcache")
    public Uni<BlogPost> getPostByCode(String code) {
        log.debugf("Get Post, code=%s", code);
        return getCollection().find(Filters.eq("code", code)).map(BlogPostMongoService::convertDocument).collectItems().first();
    }

    private ReactiveMongoCollection<Document> getCollection() {
        return mongoClient.getDatabase(db).getCollection(collection);
    }
}
