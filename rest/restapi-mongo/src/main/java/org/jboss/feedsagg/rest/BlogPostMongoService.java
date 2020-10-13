package org.jboss.feedsagg.rest;

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
import org.jboss.feedsagg.rest.model.BlogPost;
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
        p.setTags(doc.getList("tags", String.class));

        return p;
    }

    @CacheResult(cacheName = "search")
    public Uni<List<BlogPost>> search(Integer from, Integer size, String sort, String feed, String group) {
        Set<Bson> filters = new HashSet<>();
        if (StringUtils.isNotBlank(feed)) {
            filters.add(Filters.eq("feed", feed));
        }
        if (StringUtils.isNotBlank(group)) {
            filters.add(Filters.eq("group", group));
        }
        Bson filter = new Document();
        if (filters.size() > 0) {
            filter = Filters.and(filters);
        }

        FindOptions options = new FindOptions();
        if (size == null) {
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

    private ReactiveMongoCollection<Document> getCollection() {
        return mongoClient.getDatabase(db).getCollection(collection);
    }
}
