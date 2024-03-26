package com.example.unifolder;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

public class DocumentsRemoteDataSource {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference documentsCollection = db.collection("documents");
    private final ListeningExecutorService executor = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());

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
    public ListenableFuture<Void> uploadDocument(Document document) {
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
    }

}
