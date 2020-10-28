package org.jboss.feedsagg.backend.mongo;

import org.jboss.feedsagg.common.SkipItemException;
import org.junit.Assert;
import org.junit.Test;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndEntryImpl;

/**
 * Test of {@link FeedPostMongoProcessor}
 */
public class FeedPostMongoProcessorTest {

    @Test
    public void testConvertTitleToCode() {
        Assert.assertEquals("feed-one_two", FeedPostMongoProcessor.title2Code("Feed", "One Two"));
        Assert.assertEquals("feed-one_two", FeedPostMongoProcessor.title2Code("  Feed ","    One    Two   "));
        Assert.assertEquals("feed_name-one_two", FeedPostMongoProcessor.title2Code("feed_name","One_Two"));
        Assert.assertEquals("feed_name-one_two", FeedPostMongoProcessor.title2Code("_feed_name_", "_One Two_"));
        Assert.assertEquals("feed_name-one_two", FeedPostMongoProcessor.title2Code("-feed-name-", "-One Two_"));
        Assert.assertEquals("feed_name-one_two", FeedPostMongoProcessor.title2Code("feed ___ name", "One  ___ Two"));
    }

    protected SyndEntry getPost() {
        SyndEntry post = new SyndEntryImpl();
        post.setTitle("test-title");
        post.setLink("https://quarkus.io/blog/biased-locking-help/");
        return post;
    }

    @Test(expected = SkipItemException.class)
    public void testTitleValidity() throws SkipItemException {
        SyndEntry post = getPost();
        post.setTitle("");
        FeedPostMongoProcessor.validateAndConvert(post, "test", null, null);
    }

    @Test(expected = SkipItemException.class)
    public void testLinkValidity() throws SkipItemException {
        SyndEntry post = getPost();
        post.setLink("");
        FeedPostMongoProcessor.validateAndConvert(post, "test", null, null);
    }

}