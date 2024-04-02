package com.example.unifolder.Data.User;

import androidx.lifecycle.MutableLiveData;

import com.example.unifolder.Model.Result;
import com.example.unifolder.Model.User;

public interface IUserRepository {
    MutableLiveData<Result> getUser(String email, String password, boolean isUserRegistered);
    MutableLiveData<Result> getUser(String firstName, String lastName,String email, String password, int id_avatar, boolean isUserRegistered);
    MutableLiveData<Result> getUserData(User user);
    MutableLiveData<Result> logout();
    MutableLiveData<Result> deleteAccount();
    User getLoggedUser();
    void resetPassword(String email);
    MutableLiveData<Result> setUserAvatar(User user, int selectedImage);
    void signUp(String firstName, String lastName, String email, int id_avatar, String password);
    void signIn(String email, String password);
}
