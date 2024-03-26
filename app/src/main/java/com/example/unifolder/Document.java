package com.example.unifolder;

import androidx.room.Entity;

@Entity(tableName = "documents")
public class Document {
    private String id;
    private String title;
    private String author;
    private String course;
    private String tag;
    private String fileUrl;

    public Document(String title, String author, String course, String tag, String fileUrl) {
        this.title = title;
        this.author = author;
        this.course = course;
        this.tag = tag;
        this.fileUrl = fileUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }
}
