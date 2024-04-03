package com.example.unifolder;

import android.content.Context;

import com.example.unifolder.Source.Document.DocumentLocalDataSource;
import com.example.unifolder.Source.Document.DocumentsRemoteDataSource;
import com.example.unifolder.Util.ServiceLocator;

public class DocumentRepository {
    private DocumentLocalDataSource localDataSource;
    private DocumentsRemoteDataSource remoteDataSource;

    public DocumentRepository(Context context) {
        this.localDataSource = ServiceLocator.getInstance().getLocalDataSource(context);
        this.remoteDataSource = ServiceLocator.getInstance().getRemoteDataSource();
    }

    public DocumentRepository(DocumentLocalDataSource localDataSource, DocumentsRemoteDataSource remoteDataSource) {
        this.localDataSource = localDataSource;
        this.remoteDataSource = remoteDataSource;
    }

    public void uploadDocument(Document document) {
        // Invia il documento al DataSource remoto per il caricamento
        remoteDataSource.uploadDocument(document, new UploadDocumentCallback() {
            @Override
            public void onDocumentUploaded(Document uploadedDocument) {
                // Una volta che il documento è stato caricato con successo, ottieni l'id generato e salva il documento nel DataSource locale
                localDataSource.saveDocument(uploadedDocument);
            }

            @Override
            public void onUploadFailed(String errorMessage) {
                // Gestisci il fallimento dell'upload, ad esempio mostrando un messaggio di errore all'utente
            }
        });
    }
}
