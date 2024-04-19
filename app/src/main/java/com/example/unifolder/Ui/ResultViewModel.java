package com.example.unifolder.Ui;

import android.app.Activity;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.unifolder.Document;
import com.example.unifolder.DocumentRepository;

import java.util.List;


public class ResultViewModel extends ViewModel {

    private DocumentRepository documentRepository;
    private MutableLiveData<List<Document>> searchResultsLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public ResultViewModel() {
        // Costruttore vuoto
    }
    public ResultViewModel(Activity activity) {
        documentRepository = new DocumentRepository(activity); // Inizializza la repository
    }

    public LiveData<List<Document>> getSearchResultsLiveData() {
        return searchResultsLiveData;
    }

    public void setSearchResultsLiveData(List<Document> documentList){
        searchResultsLiveData.setValue(documentList);
    }

    public LiveData<Boolean> isLoading() {
        return isLoading;
    }

    public void searchDocuments(String query) {
        isLoading.setValue(true); // Imposta lo stato di caricamento su true
        // Ottieni i risultati della ricerca dalla repository

        documentRepository.searchDocumentByTitle(query);
    }
}

