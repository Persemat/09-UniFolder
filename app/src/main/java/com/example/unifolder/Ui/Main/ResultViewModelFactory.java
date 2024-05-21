package com.example.unifolder.Ui.Main;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.unifolder.Ui.Main.ResultViewModel;

public class ResultViewModelFactory implements ViewModelProvider.Factory{
    private final Context context;
    private static ResultViewModel resultViewModelInstance;

    public ResultViewModelFactory(Context context){ this.context = context;}

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if(modelClass.isAssignableFrom(ResultViewModel.class)){
            if (resultViewModelInstance == null) {
                resultViewModelInstance = new ResultViewModel(context);
            }
            // Ritorna sempre la stessa istanza memorizzata
            return (T) resultViewModelInstance;
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
