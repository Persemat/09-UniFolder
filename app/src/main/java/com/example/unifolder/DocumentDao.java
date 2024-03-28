package com.example.unifolder;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface DocumentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertDocument(Document document);

    @Query("SELECT * FROM documents WHERE id = :documentId")
    Document getDocumentById(String documentId);
}
