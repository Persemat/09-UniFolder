package com.example.unifolder;

import android.graphics.Bitmap;

import java.util.List;

public interface OnDocumentRenderedCallback {
    void OnDocumentRendered(Document document, List<Bitmap> bitmaps);
    void OnFailed(String ErrorMessage);
}
