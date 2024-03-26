package com.example.unifolder;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.unifolder.Document;
import com.example.unifolder.DocumentDao;

@Database(entities = {Document.class}, version = 1)
public abstract class DocumentDatabase extends RoomDatabase {
    public abstract DocumentDao documentDao();
}
