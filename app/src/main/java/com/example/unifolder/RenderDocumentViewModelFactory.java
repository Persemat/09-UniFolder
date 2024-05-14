package com.example.unifolder;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.unifolder.Ui.RenderDocumentViewModel;

public class RenderDocumentViewModelFactory implements ViewModelProvider.Factory {
    private final Context context;
    private static RenderDocumentViewModel renderDocumentViewModelInstance;

    public RenderDocumentViewModelFactory(Context context){ this.context = context;}

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if(modelClass.isAssignableFrom(RenderDocumentViewModel.class)){
            if (renderDocumentViewModelInstance == null) {
                renderDocumentViewModelInstance = new RenderDocumentViewModel(context);
            }
            // Ritorna sempre la stessa istanza memorizzata
            return (T) renderDocumentViewModelInstance;
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
