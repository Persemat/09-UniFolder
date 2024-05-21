package com.example.unifolder.Data.Database.Document;

import static com.example.unifolder.Util.Costants.DOCUMENTS_DATABASE_NAME;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.unifolder.Model.Document;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Document.class}, version = 1, exportSchema = false)
public abstract class DocumentDatabase extends RoomDatabase {
    public abstract DocumentDao documentDao();

    private static volatile DocumentDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = Runtime.getRuntime().availableProcessors();
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static DocumentDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (DocumentDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            DocumentDatabase.class, DOCUMENTS_DATABASE_NAME).build();
                }
            }
        }
        return INSTANCE;
    }
}
