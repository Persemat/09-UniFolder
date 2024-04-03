package com.example.unifolder;

import android.content.Context;

public interface SavedDocumentCallback {
    void onDocumentSaved(Document savedDocument);
    void onSaveFailed(String errorMessage);
}
