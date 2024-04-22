package com.example.unifolder;

import java.util.List;

public interface SearchResultCallback {
    void OnSearchCompleted(List<Document> documents);
    void OnSearchFailed(String error);
}
