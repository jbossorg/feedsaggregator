package org.jboss.feedsagg.api;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import org.jboss.feedsagg.api.model.BlogPost;

import io.quarkus.test.Mock;
import io.smallrye.mutiny.Uni;

@Mock
@ApplicationScoped
public class MockBlogPostMongoService extends BlogPostMongoService {

    @Override
    public Uni<List<BlogPost>> search(Integer from, Integer size, String sort, List<String> feed, List<String> groups, List<String> tags) {
        List<BlogPost> blogs = new ArrayList<>();
        BlogPost bp = new BlogPost("test-id1").code("test-code1");
        StringBuilder content = new StringBuilder("Conditions:");
        if (tags != null && !tags.isEmpty())
            content.append(" tags: " + tags);
        bp.setContent(content.toString());
        blogs.add(bp);
        return Uni.createFrom().item(blogs);
    }

    @Override
    public Uni<BlogPost> getPostById(String id) {
        BlogPost blog = new BlogPost(id).code("test-code");
        return Uni.createFrom().item(blog);
    }

    @Override
    public Uni<BlogPost> getPostByCode(String code) {
        BlogPost blog = null;
        if (!"unknown".equals(code)) {
            blog = new BlogPost("test-id").code(code);
        }
        return Uni.createFrom().item(blog);
    }
}
