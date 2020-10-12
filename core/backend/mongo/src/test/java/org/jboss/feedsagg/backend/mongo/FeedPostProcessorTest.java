package org.jboss.feedsagg.backend.mongo;

import org.jboss.feedsagg.common.PostValidationException;
import org.junit.Assert;
import org.junit.Test;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndEntryImpl;

/**
 * Test of {@link FeedPostProcessor}
 */
public class FeedPostProcessorTest {

    @Test
    public void testConvertTitleToCode() {
        Assert.assertEquals("one_two", FeedPostProcessor.title2Code("One Two"));
        Assert.assertEquals("one_two", FeedPostProcessor.title2Code("    One    Two   "));
        Assert.assertEquals("one_two", FeedPostProcessor.title2Code("One_Two"));
        Assert.assertEquals("one_two", FeedPostProcessor.title2Code("_One Two_"));
        Assert.assertEquals("one_two", FeedPostProcessor.title2Code("One  ___ Two"));
    }

    protected SyndEntry getPost() {
        SyndEntry post = new SyndEntryImpl();
        post.setTitle("test-title");
        post.setLink("https://quarkus.io/blog/biased-locking-help/");
        return post;
    }

    @Test(expected = PostValidationException.class)
    public void testTitleValidity() throws PostValidationException {
        SyndEntry post = getPost();
        post.setTitle("");
        FeedPostProcessor.validateAndConvert(post, "test", null, null);
    }

    @Test(expected = PostValidationException.class)
    public void testLinkValidity() throws PostValidationException {
        SyndEntry post = getPost();
        post.setLink("");
        FeedPostProcessor.validateAndConvert(post, "test", null, null);
    }

}