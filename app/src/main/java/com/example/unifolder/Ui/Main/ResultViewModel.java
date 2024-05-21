package com.example.unifolder.Ui.Main;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.unifolder.Model.Document;
import com.example.unifolder.Data.Repository.Document.DocumentRepository;
import com.example.unifolder.Util.PdfProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


public class ResultViewModel extends ViewModel {
    private static final String TAG = ResultViewModel.class.getSimpleName();
    private DocumentRepository documentRepository;
    private final MutableLiveData<List<Document>> searchResultsLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Bitmap>> documentPreviewsLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private PdfProcessor pdfProcessor;

    public ResultViewModel() {
        //costruttore vuoto
    }

    public ResultViewModel(Context context) {
        documentRepository = new DocumentRepository(context);
        pdfProcessor = new PdfProcessor();
    }

    public LiveData<List<Document>> getSearchResultsLiveData() {
        return searchResultsLiveData;
    }
    public LiveData<List<Bitmap>> getDocumentPreviewsLiveData() {
        return documentPreviewsLiveData;
    }

    public void setSearchResultsLiveData(List<Document> documentList){
        searchResultsLiveData.postValue(documentList);
        Log.d(TAG,"posted value");
    }

    public LiveData<Boolean> isLoading() {
        return isLoading;
    }

    public void searchDocuments(String query) {
        isLoading.setValue(true); // Imposta lo stato di caricamento su true
        // Ottieni i risultati della ricerca dalla repository
        if(documentRepository != null){
            documentRepository.searchDocumentByTitle(query, new SearchResultCallback(){

                @Override
                public void OnSearchCompleted(List<Document> documents) {
                    setSearchResultsLiveData(documents);
                    extractDocumentPreviews(documents);
                    Log.d(TAG, "data set");

                }

                @Override
                public void OnSearchFailed(String error) {

                }
            });
        }else{
            Log.e("ResultViewModel", "DocumentRepository is null");
        }
    }

    public void searchDocuments(String course, String tag) {
        isLoading.setValue(true); // Imposta lo stato di caricamento su true
        // Ottieni i risultati della ricerca dalla repository
        if(documentRepository != null){
            documentRepository.searchDocumentByFilter(course, tag, new SearchResultCallback(){

                @Override
                public void OnSearchCompleted(List<Document> documents) {
                    setSearchResultsLiveData(documents);
                    extractDocumentPreviews(documents);
                    Log.d(TAG, "data set");

                }

                @Override
                public void OnSearchFailed(String error) {

                }
            });
        }else{
            Log.e("ResultViewModel", "DocumentRepository is null");
        }
    }

    public void searchDocuments(String course, String tag,String query) {
        if(query.length()<2) {
            searchDocuments(course,tag);
        } else {
            isLoading.setValue(true); // Imposta lo stato di caricamento su true
            // Ottieni i risultati della ricerca dalla repository
            if(documentRepository != null){
                documentRepository.searchDocumentByTitleAndFilter(query, course, tag, new SearchResultCallback(){

                    @Override
                    public void OnSearchCompleted(List<Document> documents) {
                        setSearchResultsLiveData(documents);
                        extractDocumentPreviews(documents);
                        Log.d(TAG, "data set");

                    }

                    @Override
                    public void OnSearchFailed(String error) {

                    }
                });
            }else{
                Log.e("ResultViewModel", "DocumentRepository is null");
            }
        }

    }

    private void extractDocumentPreviews(List<Document> documents) {
        List<Future<Bitmap>> previewFutures = new ArrayList<>();

        // Per ogni documento, avvia il processo di estrazione dell'anteprima
        for (Document document : documents) {
            Future<Bitmap> previewFuture = pdfProcessor.extractFirstPageImageFromPdf(document.getFileUrl());
            previewFutures.add(previewFuture);
        }

        // Attendi il completamento di tutti i processi di estrazione delle anteprime
        List<Bitmap> previews = new ArrayList<>();
        for (Future<Bitmap> future : previewFutures) {
            try {
                Bitmap preview = future.get();
                previews.add(preview);
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Error extracting preview", e);
                // Aggiungi una bitmap vuota in caso di errore
                previews.add(null);
            }
        }

        // Imposta i risultati nella LiveData
        searchResultsLiveData.postValue(documents);
        documentPreviewsLiveData.postValue(previews);
        isLoading.postValue(false); // Imposta lo stato di caricamento su false
    }

}

