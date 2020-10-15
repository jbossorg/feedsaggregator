package org.jboss.feedsagg.rest;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import org.jboss.feedsagg.rest.model.BlogPost;

import io.quarkus.test.Mock;
import io.smallrye.mutiny.Uni;

@Mock
@ApplicationScoped
public class MockBlogPostMongoService extends BlogPostMongoService {

    @Override
    public Uni<List<BlogPost>> search(Integer from, Integer size, String sort, String feed, String group) {
        List<BlogPost> blogs = new ArrayList<>();
        blogs.add(new BlogPost("test-id1").code("test-code1"));
        return Uni.createFrom().item(blogs);
    }

    @Override
    public Uni<BlogPost> getPostById(String id) {
        BlogPost blog = new BlogPost(id).code("test-code");
        return Uni.createFrom().item(blog);
    }
}
