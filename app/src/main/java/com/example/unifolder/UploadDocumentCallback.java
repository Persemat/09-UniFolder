package com.example.unifolder;

public interface UploadDocumentCallback {
    void onDocumentUploaded(Document uploadedDocument);
    void onUploadFailed(String errorMessage);
}
