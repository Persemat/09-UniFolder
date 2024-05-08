package com.example.unifolder.Ui;

import android.graphics.Bitmap;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.unifolder.Document;

import java.util.List;

public class RenderDocumentViewModel extends ViewModel {
    private MutableLiveData<Document> documentMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<List<Bitmap>> bitmapMutableLiveData = new MutableLiveData<>();

    public MutableLiveData<Document> getDocumentMutableLiveData() {
        return documentMutableLiveData;
    }

    public void setDocumentMutableLiveData(Document document) {
        this.documentMutableLiveData.postValue(document);
    }

    public MutableLiveData<List<Bitmap>> getBitmapMutableLiveData() {
        return bitmapMutableLiveData;
    }

    public void setBitmapMutableLiveData(List<Bitmap> bitmap) {
        this.bitmapMutableLiveData.postValue(bitmap);
    }

}
