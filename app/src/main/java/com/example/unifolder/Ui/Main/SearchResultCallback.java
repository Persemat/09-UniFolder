package com.example.unifolder.Ui.Main;

import com.example.unifolder.Model.Document;

import java.util.List;

public interface SearchResultCallback {
    void OnSearchCompleted(List<Document> documents);
    void OnSearchFailed(String error);
}
