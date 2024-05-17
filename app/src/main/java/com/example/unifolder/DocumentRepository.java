package com.example.unifolder;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.unifolder.Source.Document.DocumentLocalDataSource;
import com.example.unifolder.Source.Document.DocumentRemoteDataSource;
import com.example.unifolder.Util.ServiceLocator;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class DocumentRepository {
    private static final String TAG = DocumentRepository.class.getSimpleName();
    private final DocumentLocalDataSource localDataSource;
    private final DocumentRemoteDataSource remoteDataSource;


    public DocumentRepository(Context context) {
        this.localDataSource = ServiceLocator.getInstance().getLocalDataSource(context);
        this.remoteDataSource = ServiceLocator.getInstance().getRemoteDataSource();
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

    public void searchDocumentByFilter(String course, String tag, SearchResultCallback callback) {
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
                    callback.OnSearchCompleted(documents);
                } else {
                    callback.OnSearchFailed("no docs to add");
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

    public void uploadDocument(Document document, Context context, SavedDocumentCallback callback) {
        // Invia il documento al DataSource remoto per il caricamento
        remoteDataSource.uploadDocument(document, new UploadDocumentCallback() {
            @Override
            public void onDocumentUploaded(Document uploadedDocument) {
                Log.d(TAG,"uploadedDoc: " + uploadedDocument.getId()+"; "+uploadedDocument.getFileUrl());
                // Una volta che il documento è stato caricato con successo, ottieni l'id generato e salva il documento nel DataSource locale
                localDataSource.saveDocument(uploadedDocument, context, callback);
                // ad operazione terminata, alla callback (passata da ViewModel) viene ritornato il Document
            }

            @Override
            public void onUploadFailed(String errorMessage) {
                // Gestisci il fallimento dell'upload, ad esempio mostrando un messaggio di errore all'utente
            }
        });
    }

    public void searchDocumentByTitleAndFilter(String query, String course, String tag, SearchResultCallback callback) {
        // Utilizziamo CallbackToFutureAdapter per convertire il ListenableFuture in un CompletableFuture
        ListenableFuture<List<Document>> future = remoteDataSource.searchDocumentsByTitleAndFilter(query,course,tag);
        Futures.addCallback(future, new FutureCallback<List<Document>>() {
            @Override
            public void onSuccess(@Nullable List<Document> documents) {
                Log.d(TAG,"onSuccess()");
                // Azioni da eseguire quando il futuro ha successo
                if (documents != null) {
                    Log.d(TAG,"adding docs");
                    // Utilizza i documenti restituiti
                    callback.OnSearchCompleted(documents);
                } else {
                    callback.OnSearchFailed("no docs to add");
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

    private CompletableFuture<List<Document>> getLastOpenedDocumentsAsync(String author) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return localDataSource.getLastOpenedDocuments(author).get();
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void getLastOpenedDocuments(String author, SearchResultCallback callback) {
        CompletableFuture<List<Document>> future = getLastOpenedDocumentsAsync(author);
        future.thenAccept(documents -> callback.OnSearchCompleted(documents));
    }

    private CompletableFuture<List<Document>> getYourUploadedDocumentsAsync(String author) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return localDataSource.getUploadedDocuments(author).get();
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }
    public void getYourUploadedDocuments(String author, SearchResultCallback callback) {
        CompletableFuture<List<Document>> future = getYourUploadedDocumentsAsync(author);
        future.thenAccept(documents -> callback.OnSearchCompleted(documents));
    }
    public void renderDocument(Document document, Context context, OnDocumentRenderedCallback onDocumentRenderedCallback){
        PdfProcessor pdfProcessor;

        pdfProcessor = new PdfProcessor();

        //TODO controllo se abbiamo gia documento in room o è da scaricare
        CompletableFuture<Document> Future = saveDocumentAsync(document, context);
        Future.thenAccept(document1 ->  {
            // Per ogni documento, avvia il processo di estrazione dell'anteprima
            java.util.concurrent.Future<List<Bitmap>> pagesFuture = pdfProcessor.extractAllPagesImagesFromPdf(document1.getFileUrl(), context);

            // Attendi il completamento di tutti i processi di estrazione delle anteprime
            List<Bitmap> pagesDocument = new ArrayList<>();
                try {
                    pagesDocument = pagesFuture.get();
                    onDocumentRenderedCallback.OnDocumentRendered(document, pagesDocument );
                } catch (ExecutionException | InterruptedException e) {
                    Log.e(TAG, "Error extracting page", e);
                    // Aggiungi una bitmap vuota in caso di errore
                    pagesDocument.add(null);
                }
        });
    }
    private CompletableFuture<Document> saveDocumentAsync(Document document, Context context){
        return CompletableFuture.supplyAsync(()->{
            try{
                return localDataSource.saveDocument(document, context).get();
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
