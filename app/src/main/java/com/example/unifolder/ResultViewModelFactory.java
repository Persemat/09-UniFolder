package com.example.unifolder;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.unifolder.Ui.ResultViewModel;

public class ResultViewModelFactory implements ViewModelProvider.Factory{
    private final Context context;

    public ResultViewModelFactory(Context context){ this.context = context;}

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if(modelClass.isAssignableFrom(ResultViewModel.class)){
            return (T) new ResultViewModel(context);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
