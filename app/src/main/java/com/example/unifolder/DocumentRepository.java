package com.example.unifolder;

import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.concurrent.futures.CallbackToFutureAdapter;

import com.example.unifolder.Adapter.DocumentAdapter;
import com.example.unifolder.Source.Document.DocumentLocalDataSource;
import com.example.unifolder.Source.Document.DocumentRemoteDataSource;
import com.example.unifolder.Util.ServiceLocator;
import com.google.android.material.snackbar.Snackbar;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class DocumentRepository {
    private static final String TAG = DocumentRepository.class.getSimpleName();
    private DocumentLocalDataSource localDataSource;
    private DocumentRemoteDataSource remoteDataSource;

    public DocumentRepository(Context context) {
        this.localDataSource = ServiceLocator.getInstance().getLocalDataSource(context);
        this.remoteDataSource = ServiceLocator.getInstance().getRemoteDataSource();
    }

    public DocumentRepository(DocumentLocalDataSource localDataSource, DocumentRemoteDataSource remoteDataSource) {
        this.localDataSource = localDataSource;
        this.remoteDataSource = remoteDataSource;
    }

    public void searchDocumentByTitle(String searchQuery, DocumentAdapter adapter) {
        // Utilizziamo CallbackToFutureAdapter per convertire il ListenableFuture in un CompletableFuture
        ListenableFuture<List<Document>> future = remoteDataSource.searchDocumentsByTitle(searchQuery);
        Futures.addCallback(future, new FutureCallback<List<Document>>() {
            @Override
            public void onSuccess(@Nullable List<Document> documents) {
                Log.d(TAG,"onSuccess()");
                // Azioni da eseguire quando il futuro ha successo
                if (documents != null) {
                    Log.d(TAG,"adding docs");
                    // Utilizza i documenti restituiti
                    adapter.addDocuments(documents);
                } else {
                    Log.d(TAG,"no docs to add");
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d(TAG,"onFailure(): "+t.getMessage());
                // Azioni da eseguire in caso di fallimento del futuro
                // Gestisci l'eccezione o avvia un'azione alternativa
            }
        }, Executors.newSingleThreadExecutor());
    }

    public void searchDocumentByFilter(String course, String tag, DocumentAdapter adapter) {
        // Utilizziamo CallbackToFutureAdapter per convertire il ListenableFuture in un CompletableFuture
        ListenableFuture<List<Document>> future = remoteDataSource.searchDocumentsByCourseAndTag(course,tag);
        Futures.addCallback(future, new FutureCallback<List<Document>>() {
            @Override
            public void onSuccess(@Nullable List<Document> documents) {
                Log.d(TAG,"onSuccess()");
                // Azioni da eseguire quando il futuro ha successo
                if (documents != null) {
                    Log.d(TAG,"adding docs");
                    // Utilizza i documenti restituiti
                    adapter.addDocuments(documents);
                } else {
                    Log.d(TAG,"no docs to add");
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d(TAG,"onFailure(): "+t.getMessage());
                // Azioni da eseguire in caso di fallimento del futuro
                // Gestisci l'eccezione o avvia un'azione alternativa
            }
        }, Executors.newSingleThreadExecutor());
    }

    public Document uploadDocument(Document document) {
        // Valore di ritorno
        final Document[] d = {null};


        // Invia il documento al DataSource remoto per il caricamento
        remoteDataSource.uploadDocument(document, new UploadDocumentCallback() {
            @Override
            public void onDocumentUploaded(Document uploadedDocument) {
                // Una volta che il documento è stato caricato con successo, ottieni l'id generato e salva il documento nel DataSource locale
                localDataSource.saveDocument(uploadedDocument, new SavedDocumentCallback() {

                    @Override
                    public void onDocumentSaved(Document savedDocument) {
                        d[0] = savedDocument;
                    }

                    @Override
                    public void onSaveFailed(String errorMessage) {

                    }
                });
            }

            @Override
            public void onUploadFailed(String errorMessage) {
                // Gestisci il fallimento dell'upload, ad esempio mostrando un messaggio di errore all'utente
            }
        });

        // todo: not returning any doc
        return d[0];
    }
}
