package org.jboss.feedsagg.backend.mongo;

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
import org.jboss.feedsagg.common.SkipItemException;

import com.rometools.rome.feed.synd.SyndCategory;
import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndEntry;

/**
 * Transforms {@link SyndEntry} into {@link Document}
 */
public class FeedPostMongoProcessor implements ItemProcessor {

    @Inject
    JobContext jobContext;

    @Override
    public Object processItem(Object item) throws Exception {
        SyndEntry post = (SyndEntry) item;
        Properties jobProperties = org.jboss.feedsagg.common.JobUtils.getJobParameter(jobContext);

        String feed = jobProperties.getProperty("feed");
        if (feed == null) {
            throw new BatchRuntimeException("job parameter `feed` must be defined");
        }
        String group = jobProperties.getProperty("group");
        String defaultAuthor = jobProperties.getProperty("author");
        return validateAndConvert(post, feed, group, defaultAuthor);
    }

    public static Document validateAndConvert(SyndEntry post, String feed, String group, String defaultAuthor) throws SkipItemException {
        Document document = new Document("feed", feed);
        if (StringUtils.isNotBlank(group)) {
            document.append("group", group);
        }

        String link = StringUtils.trimToEmpty(post.getLink());
        if (StringUtils.isBlank(link)) {
            throw new SkipItemException("Link is empty");
        }
        document.append("url", link);

        String title = StringUtils.trimToEmpty(post.getTitle());
        if (StringUtils.isBlank(title)) {
            throw new SkipItemException("Title is empty.", link);
        }
        document.append("title", title);

        document.append("code", title2Code(feed, title));

        String author = StringUtils.trimToNull(post.getAuthor());
        if (author == null && defaultAuthor != null) {
            author = defaultAuthor;
        }
        document.append("author", author);

        Date publishedDate = post.getPublishedDate();
        if (publishedDate == null) {
            throw new SkipItemException("Published is empty", link);
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

    public static String title2Code(String feed, String title) {
        if (title == null) {
            return null;
        }
        return normalizeTitle(feed) + "-" + normalizeTitle(title);
    }

    public static String normalizeTitle(String string) {
        char[] titleWithUnderscores = string.toLowerCase().replaceAll("[^a-z0-9_]", "_").toCharArray();

        StringBuffer newTitle = new StringBuffer();

        // Removing _ from the beginning.
        int titleIndex = 0;
        while ((titleIndex < titleWithUnderscores.length) && (titleWithUnderscores[titleIndex] == '_')) {
            titleIndex++;
        }

        // Removing multiple _ in the text.
        boolean previousLetter = true;
        while (titleIndex < titleWithUnderscores.length) {
            if (titleWithUnderscores[titleIndex] == '_') {
                if (previousLetter) {
                    newTitle.append(titleWithUnderscores[titleIndex]);
                }

                previousLetter = false;
            } else {
                newTitle.append(titleWithUnderscores[titleIndex]);
                previousLetter = true;
            }

            titleIndex++;
        }

        // Removing _ from the end, if there was one.
        if ((newTitle.length() > 0) && (newTitle.charAt(newTitle.length() - 1) == '_')) {
            newTitle.deleteCharAt(newTitle.length() - 1);
        }

        return newTitle.toString();
    }
}
