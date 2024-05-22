package com.example.unifolder.Data.User;


import androidx.lifecycle.MutableLiveData;

import com.example.unifolder.Model.Result;
import com.example.unifolder.Model.User;
import com.example.unifolder.Source.Document.DocumentLocalDataSource;
import com.example.unifolder.Source.User.BaseUserAuthenticationRemoteDataSource;
import com.example.unifolder.Source.User.BaseUserDataRemoteDataSource;

public class UserRepository implements IUserRepository,UserResponseCallback{

    private final BaseUserAuthenticationRemoteDataSource userRemoteDataSource;
    private final BaseUserDataRemoteDataSource userDataRemoteDataSource;
    private final DocumentLocalDataSource localDataSource;
    private final MutableLiveData<Result> userFavoritesMutableLiveData;
    private final MutableLiveData<Result> userMutableLiveData;

    public UserRepository(BaseUserAuthenticationRemoteDataSource userRemoteDataSource, BaseUserDataRemoteDataSource userDataRemoteDataSource, DocumentLocalDataSource localDataSource){
        this.userRemoteDataSource = userRemoteDataSource;
        this.userDataRemoteDataSource = userDataRemoteDataSource;
        this.localDataSource = localDataSource;
        this.userMutableLiveData = new MutableLiveData<>();
        this.userFavoritesMutableLiveData = new MutableLiveData<>();
        this.userRemoteDataSource.setUserResponseCallback(this);
        this.userDataRemoteDataSource.setUserResponseCallback(this);
    }

    @Override
    public MutableLiveData<Result> getUser(String email, String password, boolean isUserRegistered) {
        signIn(email, password);
        return userMutableLiveData;
    }
    public MutableLiveData<Result> getUser(String firstName, String lastName,String username, String email, String password, int id_avatar, boolean isUserRegistered) {
        signUp(firstName, lastName, username, email, id_avatar, password);
        return userMutableLiveData;
    }
    public MutableLiveData<Result> getUserData(User user){
        userDataRemoteDataSource.getUserRealtime(user);
        return userMutableLiveData;
    }

    @Override
    public MutableLiveData<Result> logout() {
        userRemoteDataSource.logout();
        return userMutableLiveData;
    }
    @Override
    public MutableLiveData<Result> deleteAccount() {
        userRemoteDataSource.deleteAccount();
        return userMutableLiveData;
    }

    @Override
    public User getLoggedUser() {
        return userRemoteDataSource.getLoggedUser();
    }

    @Override
    public void resetPassword(String email) {
        userRemoteDataSource.sendEmailPasswordReset(email);
    }

    @Override
    public MutableLiveData<Result> setUserAvatar(User user, int selectedImage) {
        userDataRemoteDataSource.setUserAvatar(user, selectedImage);
        return userMutableLiveData;
    }

    @Override
    public void signUp(String firstName, String lastName, String username, String email, int id_avatar, String password) {
        userRemoteDataSource.signUp(firstName, lastName, username, email, id_avatar, password);
    }

    @Override
    public void signIn(String email, String password) {
        userRemoteDataSource.signIn(email, password);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onSuccessFromAuthentication(User user) {
        if (user != null) {
            userDataRemoteDataSource.saveUserData(user);
        }
    }

    @Override
    public void onFailureFromAuthentication(String message) {
        Result.Error result = new Result.Error(message);
        userMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessFromRemoteDatabase(User user) {
        Result.UserResponseSuccess result = new Result.UserResponseSuccess(user);
        userMutableLiveData.postValue(result);
    }

    @Override
    public void onFailureFromRemoteDatabase(String message) {
        Result.Error result = new Result.Error(message);
        userMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessLogout() {
        localDataSource.deleteAll();
        /*
        Result.UserResponseSuccess result = new Result.UserResponseSuccess(null);
        userMutableLiveData.postValue(result);

         */
    }

    @Override
    public void onSuccessDeleteUser(User user) {
        localDataSource.deleteAll();
        userDataRemoteDataSource.deleteUserRealtime(user);
    }

    @Override
    public void onSuccessDeleteUserRealtime() {
        Result.UserResponseSuccess result = new Result.UserResponseSuccess(null);
        userMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessSetAvatar(User user) {
        userDataRemoteDataSource.getUserRealtime(user);
    }
}
