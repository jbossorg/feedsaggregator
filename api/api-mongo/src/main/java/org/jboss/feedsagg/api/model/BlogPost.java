package org.jboss.feedsagg.api.model;

import java.util.Date;
import java.util.List;

import javax.json.bind.annotation.JsonbDateFormat;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class BlogPost {
    
    @JsonIgnore
    String id;
    
    @Schema(description = "ID of the blogpost. Unique id created by this service.")
    String code;
    @Schema(description = "URL of the blogpost, can be used to view it.")
    String url;
    @Schema(description = "ID of the feed this blogpost belongs to. It is internal id in this service.")
    String feed;
    @Schema(description = "Group this blogpost belongs to. Grouping is internal feature of this service to organize feeds a bit.")
    String group;
    @Schema(description = "Tutle taken from the blogpost.")
    String title;
    @Schema(description = "Author taken from the blogpost.")
    String author;
    @Schema(description = "Timestamp when the Blogpost had been published taken from the blogpost. ISO 8601 (yyyy-MM-dd'T'HH:mm:ssZ) format.")
    @JsonbDateFormat(value = "yyyy-MM-dd'T'HH:mm:ssZ")
    Date published;
    @Schema(description = "Timestamp when the Blogpost had been updated taken from the blogpost. ISO 8601 (yyyy-MM-dd'T'HH:mm:ssZ) format.")
    @JsonbDateFormat(value = "yyyy-MM-dd'T'HH:mm:ssZ")
    Date updated;
    @Schema(description = "Tags taken from the blogpost.")
    List<String> tags;
    @Schema(description = "Blogpost content containing original html formating. Can be really long.")
    String content;
    @Schema(description = "Blogpost content cleaned not to contain html formating and shortened so it can be used as an preview.")
    String contentPreview;

    public BlogPost() {
    }

    public BlogPost(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public BlogPost code(String code) {
        this.code = code;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFeed() {
        return feed;
    }

    public void setFeed(String feed) {
        this.feed = feed;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Date getPublished() {
        return published;
    }

    public void setPublished(Date published) {
        this.published = published;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContentPreview() {
        return contentPreview;
    }

    public void setContentPreview(String contentPreview) {
        this.contentPreview = contentPreview;
    }

}
