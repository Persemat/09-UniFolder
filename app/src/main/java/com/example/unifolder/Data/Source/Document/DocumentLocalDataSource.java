package com.example.unifolder.Data.Source.Document;

import android.content.Context;
import android.util.Log;

import com.example.unifolder.Model.Document;
import com.example.unifolder.Data.Database.Document.DocumentDao;
import com.example.unifolder.Data.Database.Document.DocumentDatabase;
import com.example.unifolder.Util.LocalStorageManager;
import com.example.unifolder.Ui.Main.SavedDocumentCallback;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DocumentLocalDataSource {
    private DocumentDao documentDao;
    private ExecutorService executorService;
    private static final String TAG = DocumentLocalDataSource.class.getSimpleName();

    public DocumentLocalDataSource(Context context) {
        DocumentDatabase database = DocumentDatabase.getDatabase(context);
        documentDao = database.documentDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public Future<Void> saveDocument(Document document, SavedDocumentCallback callback) {
        Log.d(TAG,"saveDocument()");
        return executorService.submit(() -> {documentDao.insertDocument(document);
            callback.onDocumentSaved(document);
            return null;
        });
    }

    public Future<Void> saveDocument(Document document, Context context, SavedDocumentCallback callback) {
        return executorService.submit(() -> {
            // Ottieni il riferimento al file remoto dall'URL fornito nel campo fileUrl
            String fileUrl = document.getFileUrl();
            InputStream inputStream = null;
            try {
                URL url = new URL(fileUrl);
                URLConnection connection = url.openConnection();
                connection.connect();
                inputStream = connection.getInputStream();

                // Salva il file localmente e ottieni il percorso del file
                String fileName = "document_" + document.getTitle() + ".pdf";
                String filePath = LocalStorageManager.saveFileLocally(context, inputStream, fileName);

                // Aggiorna il campo fileUrl del documento con il percorso locale del file
                document.setFileUrl(filePath);

                // Salva il documento nel database Room
                documentDao.insertDocument(document);

                callback.onDocumentSaved(document);
            } catch (Exception e) {
                Log.e(TAG, "Errore durante il salvataggio del documento locale:", e);
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        Log.e(TAG, "Errore durante la chiusura dello stream di input:", e);
                    }
                }
            }
            return null;
        });
    }

    public Future<Document> saveDocument(Document document, Context context) {
        return executorService.submit(() -> {
            // Ottieni il riferimento al file remoto dall'URL fornito nel campo fileUrl
            String fileUrl = document.getFileUrl();
            InputStream inputStream = null;
            try {
                URL url = new URL(fileUrl);
                URLConnection connection = url.openConnection();
                connection.connect();
                inputStream = connection.getInputStream();

                // Salva il file localmente e ottieni il percorso del file
                String fileName = "document_" + document.getTitle() + ".pdf";
                String filePath = LocalStorageManager.saveFileLocally(context, inputStream, fileName);

                // Aggiorna il campo fileUrl del documento con il percorso locale del file
                document.setFileUrl(filePath);

                // Salva il documento nel database Room
                documentDao.insertDocument(document);
                return document;

            } catch (Exception e) {
                Log.e(TAG, "Errore durante il salvataggio del documento locale:", e);
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        Log.e(TAG, "Errore durante la chiusura dello stream di input:", e);
                    }
                }
            }
            return null;
        });
    }

    public Future<Document> getDocumentById(String documentId) {
        return executorService.submit(() -> documentDao.getDocumentById(documentId));
    }

    public Future<List<Document>> getUploadedDocuments(String author) {
        return executorService.submit(() -> documentDao.getUploadedDocuments(author));
    }

    public Future<List<Document>> getLastOpenedDocuments(String author) {
        return executorService.submit(() -> documentDao.getLastOpenedDocuments(author));
    }
}
