package com.example.unifolder.Welcome;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.unifolder.Data.User.IUserRepository;

public class UserViewModelFactory implements ViewModelProvider.Factory{
    private final IUserRepository userRepository;

    public UserViewModelFactory(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new UserViewModel(userRepository);
    }
}
