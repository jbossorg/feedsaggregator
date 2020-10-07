package org.jboss.planet.feeds2mongo.batch;

import java.util.Date;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.batch.api.chunk.ItemProcessor;
import javax.batch.operations.BatchRuntimeException;
import javax.batch.runtime.context.JobContext;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.jboss.planet.feeds2mongo.StringTools;

import com.rometools.rome.feed.synd.SyndCategory;
import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndEntry;

public class FeedPostProcessor implements ItemProcessor {

    @Inject
    JobContext jobContext;

    @Override
    public Object processItem(Object item) throws Exception {
        SyndEntry post = (SyndEntry) item;
        Properties jobProperties = FeedReader.getJobParameter(jobContext);

        String feed = jobProperties.getProperty("feed");
        if (feed == null) {
            throw new BatchRuntimeException("job parameter `feed` must be defined");
        }
        String group = jobProperties.getProperty("group");
        return validateAndConvert(post, feed, group);
    }
    public static Document validateAndConvert(SyndEntry post, String feed, String group) throws PostValidationException {
        Document document = new Document("feed", feed);
        document.append("group", group);

        String title = normalizeString(post.getTitle());
        if (StringUtils.isBlank(title)) {
            throw new PostValidationException("Title is empty");
        }
        document.append("title", title);

        document.append("code", StringTools.title2Code(title));

        String link = normalizeString(post.getLink());
        if (StringUtils.isBlank(link)) {
            throw new PostValidationException("Link is empty");
        }
        document.append("url", link);

        Date publishedDate = post.getPublishedDate();
        if (publishedDate == null) {
            throw new PostValidationException("Published is empty");
        }
        document.append("published", publishedDate);

        Date updatedDate = post.getUpdatedDate();
        if (updatedDate == null) {
            updatedDate = publishedDate;
        }
        document.append("updated", updatedDate);


        Set<String> tags = new HashSet<>();
        for (Object categoryObj : post.getCategories()) {
            SyndCategory category = (SyndCategory) categoryObj;
            String tag = StringUtils.trimToNull(category.getName());
            if (tag != null) {
                tags.add(tag);
            }
        }
        document.append("tags", tags);

        // Setting content
        String longestContent = post.getDescription() == null ? "" : post.getDescription().getValue();

        for (SyndContent content : post.getContents()) {
            String currentContent = content == null ? "" : content.getValue();

            if (currentContent.length() > longestContent.length()) {
                longestContent = currentContent;
            }
        }

        document.append("content", longestContent);

        return document;
    }

    public static String normalizeString(String input) {
        String output = StringUtils.trimToEmpty(input);
        return output;
    }
}
