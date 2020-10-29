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

    public static final String DOCFIELD_CONTENT_PREVIEW = "contentPreview";

    public static final String CONTENT = "content";

    public static final String DOCFIELD_UPDATED = "updated";

    public static final String DOCFIELD_AUTHOR = "author";

    public static final String DOCFIELD_TITLE = "title";

    public static final String DOCFIELD_URL = "url";

    public static final String DOCFIELD_ID = "_id";

    public static final String DOCFIELD_CODE = "code";

    public static final String DOCFIELD_TAGS = "tags";

    public static final String DOCFIELD_GROUP = "group";

    public static final String DOCFIELD_FEED = "feed";

    public static final String DOCFIELD_PUBLISHED = "published";

    private final Logger log = Logger.getLogger(BlogPostMongoService.class);

    @Inject
    ReactiveMongoClient mongoClient;

    @ConfigProperty(name = "app.mongo.db")
    String db;

    @ConfigProperty(name = "app.mongo.collection")
    String collection;

    public static BlogPost convertDocumentWithoutContent(Document doc) {
        BlogPost p = new BlogPost(doc.getObjectId(DOCFIELD_ID).toString());
        p.setCode(doc.getString(DOCFIELD_CODE));
        p.setUrl(doc.getString(DOCFIELD_URL));
        p.setFeed(doc.getString(DOCFIELD_FEED));
        p.setGroup(doc.getString(DOCFIELD_GROUP));
        p.setTitle(doc.getString(DOCFIELD_TITLE));
        p.setAuthor(doc.getString(DOCFIELD_AUTHOR));
        p.setPublished(doc.getDate(DOCFIELD_PUBLISHED));
        p.setUpdated(doc.getDate(DOCFIELD_UPDATED));
        p.setContentPreview(doc.getString(DOCFIELD_CONTENT_PREVIEW));
        p.setTags(doc.getList(DOCFIELD_TAGS, String.class));
        return p;
    }
    
    public static BlogPost convertDocumentWithContent(Document doc) {
        BlogPost p = convertDocumentWithoutContent(doc);
        p.setContent(doc.getString(CONTENT));
        return p;
    }

    @CacheResult(cacheName = "search")
    public Uni<List<BlogPost>> search(Integer from, Integer size, String sort, final boolean content, List<String> feeds, List<String> feedsExclude, List<String> groups, List<String> groupsExclude, List<String> tags, List<String> tagsExclude) {
        Set<Bson> filters = new HashSet<>();
        addIncludeFilterByListOfValues(filters, DOCFIELD_FEED, feeds);
        addIncludeFilterByListOfValues(filters, DOCFIELD_GROUP, groups);
        addIncludeFilterByListOfValues(filters, DOCFIELD_TAGS, tags);

        addExcludeFilterByListOfValues(filters, DOCFIELD_FEED, feedsExclude);
        addExcludeFilterByListOfValues(filters, DOCFIELD_GROUP, groupsExclude);
        addExcludeFilterByListOfValues(filters, DOCFIELD_TAGS, tagsExclude);

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
        if (from != null && from >= 0) {
            options.skip(from);
        }
        if (StringUtils.equalsIgnoreCase(sort, "asc")) {
            options.sort(Sorts.ascending(DOCFIELD_PUBLISHED));
        } else {
            options.sort(Sorts.descending(DOCFIELD_PUBLISHED));
        }

        log.debugf("Search, filter=%s, options=%s", filter, options);

        return getCollection().find(filter, options).map(content? BlogPostMongoService::convertDocumentWithContent: BlogPostMongoService::convertDocumentWithoutContent).collectItems().asList();
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
    protected void addIncludeFilterByListOfValues(Set<Bson> filters, String fieldName, List<String> values) {
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

    /**
     * Add filter to select documents not containing any value from the list of values in the defined field. Values from
     * the list are sanitized:
     * <ul>
     * <li>if list is null or empty then no filter is added
     * <li>blank value from the list is ignored
     * </ul>
     * 
     * @param filters to add additional condition to - it is expected this is AND filter!
     * @param fieldName in the document to filter over
     * @param values to filter by
     */
    protected void addExcludeFilterByListOfValues(Set<Bson> filters, String fieldName, List<String> values) {
        if (values != null && values.size() > 0) {
            Set<Bson> excludeFilters = new HashSet<>();
            for (String value : values) {
                if (StringUtils.isNotBlank(value))
                    excludeFilters.add(Filters.ne(fieldName, value));
            }
            if (excludeFilters.size() > 0) {
                filters.add(Filters.and(excludeFilters));
            }
        }
    }

    @CacheResult(cacheName = "postcache")
    public Uni<BlogPost> getPostById(String id) {
        log.debugf("Get Post, id=%s", id);

        try {
            ObjectId objectId = new ObjectId(id);

            return getCollection().find(Filters.eq(DOCFIELD_ID, objectId)).map(BlogPostMongoService::convertDocumentWithContent).collectItems().first();
        } catch (IllegalArgumentException e) {
            return Uni.createFrom().failure(e);
        }
    }

    @CacheResult(cacheName = "postcache")
    public Uni<BlogPost> getPostByCode(String code) {
        log.debugf("Get Post, code=%s", code);
        return getCollection().find(Filters.eq(DOCFIELD_CODE, code)).map(BlogPostMongoService::convertDocumentWithContent).collectItems().first();
    }

    protected ReactiveMongoCollection<Document> getCollection() {
        return mongoClient.getDatabase(db).getCollection(collection);
    }
}
