package com.example.unifolder.Ui.Main;

import com.example.unifolder.Model.Document;

public interface SavedDocumentCallback {
    void onDocumentSaved(Document savedDocument);
    void onSaveFailed(String errorMessage);
}
