package com.example.unifolder.Source.Document;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.unifolder.Document;
import com.example.unifolder.UploadDocumentCallback;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

public class DocumentsRemoteDataSource {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private final CollectionReference documentsCollection = db.collection("documents");
    private final ListeningExecutorService executor = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());
    private final String TAG = DocumentsRemoteDataSource.class.getSimpleName();

    public ListenableFuture<List<Document>> searchDocumentsByTitle(String searchQuery) {
        return executor.submit(new Callable<List<Document>>() {
            @Override
            public List<Document> call() throws Exception {
                List<Document> matchingDocuments = new ArrayList<>();

                // Esegui la query per cercare i documenti con titoli che contengono la stringa di ricerca
                Query query = documentsCollection.whereGreaterThanOrEqualTo("title", searchQuery)
                        .whereLessThanOrEqualTo("title", searchQuery + "\uf8ff");
                QuerySnapshot querySnapshot = query.get().getResult();

                for (QueryDocumentSnapshot document : querySnapshot) {
                    Document doc = document.toObject(Document.class);
                    matchingDocuments.add(doc);
                }

                return matchingDocuments;
            }
        });
    }

    public ListenableFuture<List<Document>> searchDocumentsByCourseAndTag(String course, String tag) {
        return executor.submit(new Callable<List<Document>>() {
            @Override
            public List<Document> call() throws Exception {
                List<Document> matchingDocuments = new ArrayList<>();

                // Costruisci la query per cercare i documenti con corso specificato
                Query query = documentsCollection.whereEqualTo("course", course);

                // Se specificato, aggiungi la clausola per il tag
                if (tag != null) {
                    query = query.whereEqualTo("tag", tag);
                }

                // Esegui la query
                QuerySnapshot querySnapshot = query.get().getResult();

                for (QueryDocumentSnapshot document : querySnapshot) {
                    Document doc = document.toObject(Document.class);
                    matchingDocuments.add(doc);
                }

                return matchingDocuments;
            }
        });
    }
    /*public ListenableFuture<Void> uploadDocument(Document document) {
        return executor.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                // Carica il documento nel database remoto e ottieni l'ID generato da Firebase
                DocumentReference docRef = documentsCollection.add(document).getResult();
                String documentId = docRef.getId();

                // Aggiorna il documento locale con l'ID generato da Firebase
                document.setId(documentId);

                return null;
            }
        });
    }*/

    public Task<Uri> uploadDocument(Document document, UploadDocumentCallback uploadDocumentCallback) {
        // Ottieni un riferimento al percorso nel Cloud Storage
        String fileName = "document_" + document.getTitle() + ".pdf"; // Nome del file nel Cloud Storage
        StorageReference fileRef = storage.getReference().child("documents").child(fileName);

        // Carica il file su Firebase Cloud Storage
        Uri fileUri = Uri.parse(document.getFileUrl());
        UploadTask uploadTask = fileRef.putFile(fileUri);

        // Continua con il completamento dell'uploadTask per ottenere l'URL del file
        return fileRef.putFile(fileUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    Log.d(TAG, "!task.isSuccessful()");
                    throw task.getException();
                }

                // Ottieni l'URL del file caricato
                return fileRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "onComplete -> task.isSuccessful()");

                    // Ottieni l'URL del file
                    Uri downloadUri = task.getResult();

                    // Esegui l'operazione di accesso al database Room su un thread diverso
                    Executors.newSingleThreadExecutor().execute(new Runnable() {
                        @Override
                        public void run() {
                            // Aggiungi il documento al database Firestore con l'URL del file
                            Document remoteDocument = new Document(document.getTitle(), document.getAuthor(), document.getCourse(), document.getTag(), "");
                            remoteDocument.setFileUrl(downloadUri.toString());

                            // Aggiungi il documento al database Firestore
                            try {
                                documentsCollection.add(remoteDocument).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                        DocumentReference docRef = task.getResult();
                                        String documentId = docRef.getId();

                                        // Aggiorna il documento locale con l'ID generato da Firebase
                                        document.setId(documentId);
                                        uploadDocumentCallback.onDocumentUploaded(document);
                                    }
                                });

                            } catch (Exception e) {
                                Log.e(TAG, "Errore durante l'aggiunta del documento:", e);
                                uploadDocumentCallback.onUploadFailed(e.getMessage());
                            }
                        }
                    });
                } else {
                    Log.d(TAG, "onComplete -> !task.isSuccessful()");

                    // Gestisci l'errore durante il caricamento del file
                    Exception e = task.getException();
                    Log.e(TAG, "Errore durante il caricamento del file:", e);
                    uploadDocumentCallback.onUploadFailed(e.getMessage());
                }
            }
        });
    }
}
