package com.example.unifolder.Data.Database.Document;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.unifolder.Model.Document;

import java.util.List;

@Dao
public interface DocumentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertDocument(Document document);

    @Query("SELECT * FROM documents WHERE id = :documentId")
    Document getDocumentById(String documentId);

    @Query("SELECT * FROM documents WHERE author = :authorId")
    List<Document> getUploadedDocuments(String authorId);

    @Query("SELECT * FROM documents WHERE author != :author")
    List<Document> getLastOpenedDocuments(String author);

    @Query("SELECT * FROM documents")
    List<Document> getAll();

    @Query("DELETE FROM documents")
    int deleteAll();
}
