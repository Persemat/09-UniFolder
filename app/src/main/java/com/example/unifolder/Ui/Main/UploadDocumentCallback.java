package com.example.unifolder.Ui.Main;

import com.example.unifolder.Model.Document;

public interface UploadDocumentCallback {
    void onDocumentUploaded(Document uploadedDocument);
    void onUploadFailed(String errorMessage);
}
