package com.example.unifolder;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "documents")
public class Document implements Parcelable {
    @PrimaryKey @NonNull
    private String id;
    private String title;
    private String author;
    private String course;
    private String tag;
    private String fileUrl;

    public Document() {}

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(author);
        dest.writeString(course);
        dest.writeString(tag);
        dest.writeString(fileUrl);
    }

    protected Document(Parcel in) {
        id = in.readString();
        title = in.readString();
        author = in.readString();
        course = in.readString();
        tag = in.readString();
        fileUrl = in.readString();
    }

    public static final Parcelable.Creator<Document> CREATOR = new Parcelable.Creator<Document>() {
        @Override
        public Document createFromParcel(Parcel in) {
            return new Document(in);
        }

        @Override
        public Document[] newArray(int size) {
            return new Document[size];
        }
    };

}
