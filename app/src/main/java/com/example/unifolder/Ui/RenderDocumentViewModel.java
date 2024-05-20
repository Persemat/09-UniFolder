package com.example.unifolder.Ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.unifolder.Document;
import com.example.unifolder.DocumentRepository;
import com.example.unifolder.OnDocumentRenderedCallback;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class RenderDocumentViewModel extends ViewModel {
    private static final String TAG = RenderDocumentViewModel.class.getSimpleName();
    private MutableLiveData<Document> documentMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<List<Bitmap>> bitmapMutableLiveData = new MutableLiveData<>();
    private DocumentRepository documentRepository;

    public RenderDocumentViewModel(Context context) {
        documentRepository = new DocumentRepository(context);
    }

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

    public void renderDocument(Document document, Context context){
        try {
            documentRepository.renderDocument(document, context, new OnDocumentRenderedCallback() {
                @Override
                public void OnDocumentRendered(Document document, List<Bitmap> bitmaps) {
                    Log.d(TAG,"success render");
                    setDocumentMutableLiveData(document);
                    setBitmapMutableLiveData(bitmaps);
                }

                @Override
                public void OnFailed(String ErrorMessage) {

                }
            });
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
