package org.jboss.planet.feeds2mongo.batch;

import org.junit.Test;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndEntryImpl;

public class PostsProcessorTest {

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
        PostsProcessor.validateAndConvert(post, "test");
    }
}