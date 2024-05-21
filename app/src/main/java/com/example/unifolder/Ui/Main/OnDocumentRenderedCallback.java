package com.example.unifolder.Ui.Main;

import android.graphics.Bitmap;

import com.example.unifolder.Model.Document;

import java.util.List;

public interface OnDocumentRenderedCallback {
    void OnDocumentRendered(Document document, List<Bitmap> bitmaps);
    void OnFailed(String ErrorMessage);
}
