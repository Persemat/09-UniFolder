package com.example.unifolder.Ui;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.unifolder.Document;
import com.example.unifolder.DocumentRepository;
import com.example.unifolder.SearchResultCallback;

import java.util.List;


public class ResultViewModel extends ViewModel {
    private static final String TAG = ResultViewModel.class.getSimpleName();
    private DocumentRepository documentRepository;
    private MutableLiveData<List<Document>> searchResultsLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public ResultViewModel() {
        // Costruttore vuoto
    }

    public ResultViewModel(Context context) {
        documentRepository = new DocumentRepository(context,this);
    }

    public LiveData<List<Document>> getSearchResultsLiveData() {
        return searchResultsLiveData;
    }

    public void setSearchResultsLiveData(List<Document> documentList){
        searchResultsLiveData.postValue(documentList);
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

