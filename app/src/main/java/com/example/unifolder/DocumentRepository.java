package com.example.unifolder;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.unifolder.Adapter.DocumentAdapter;
import com.example.unifolder.Source.Document.DocumentLocalDataSource;
import com.example.unifolder.Source.Document.DocumentRemoteDataSource;
import com.example.unifolder.Ui.ResultViewModel;
import com.example.unifolder.Util.ServiceLocator;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.List;
import java.util.concurrent.Executors;

public class DocumentRepository {
    private static final String TAG = DocumentRepository.class.getSimpleName();
    private final DocumentLocalDataSource localDataSource;
    private final DocumentRemoteDataSource remoteDataSource;

    private ResultViewModel resultViewModel;


    public DocumentRepository(Context context) {
        this.localDataSource = ServiceLocator.getInstance().getLocalDataSource(context);
        this.remoteDataSource = ServiceLocator.getInstance().getRemoteDataSource();
        this.resultViewModel = viewModel;

    }

    public DocumentRepository(DocumentLocalDataSource localDataSource, DocumentRemoteDataSource remoteDataSource) {
        this.localDataSource = localDataSource;
        this.remoteDataSource = remoteDataSource;
    }

    public void searchDocumentByTitle(String searchQuery, SearchResultCallback searchResultCallback) {
        // Utilizziamo CallbackToFutureAdapter per convertire il ListenableFuture in un CompletableFuture
        ListenableFuture<List<Document>> future = remoteDataSource.searchDocumentsByTitle(searchQuery);
        Futures.addCallback(future, new FutureCallback<List<Document>>() {
            @Override
            public void onSuccess(@Nullable List<Document> documents) {
                Log.d(TAG,"onSuccess()");
                // Azioni da eseguire quando il futuro ha successo
                if (documents != null) {
                    Log.d(TAG,"adding docs");
                    searchResultCallback.OnSearchCompleted(documents);
                } else {
                    searchResultCallback.OnSearchFailed("Error");
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

    public void uploadDocument(Document document, SavedDocumentCallback callback) {
        // Invia il documento al DataSource remoto per il caricamento
        remoteDataSource.uploadDocument(document, new UploadDocumentCallback() {
            @Override
            public void onDocumentUploaded(Document uploadedDocument) {
                // Una volta che il documento è stato caricato con successo, ottieni l'id generato e salva il documento nel DataSource locale
                localDataSource.saveDocument(uploadedDocument, callback);
                // ad operazione terminata, alla callback (passata da ViewModel) viene ritornato il Document
            }

            @Override
            public void onUploadFailed(String errorMessage) {
                // Gestisci il fallimento dell'upload, ad esempio mostrando un messaggio di errore all'utente
            }
        });
    }
}
