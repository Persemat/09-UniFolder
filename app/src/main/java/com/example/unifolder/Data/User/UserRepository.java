package com.example.unifolder.Data.User;


import androidx.lifecycle.MutableLiveData;

import com.example.unifolder.Model.Result;
import com.example.unifolder.Model.User;
import com.example.unifolder.Source.User.BaseUserAuthenticationRemoteDataSource;
import com.example.unifolder.Source.User.BaseUserDataRemoteDataSource;

public class UserRepository implements IUserRepository,UserResponseCallback{

    private final BaseUserAuthenticationRemoteDataSource userRemoteDataSource;
    private final BaseUserDataRemoteDataSource userDataRemoteDataSource;
    private final MutableLiveData<Result> userMutableLiveData;
    private final MutableLiveData<Result> userFavoritesMutableLiveData;

    public UserRepository(BaseUserAuthenticationRemoteDataSource userRemoteDataSource, BaseUserDataRemoteDataSource userDataRemoteDataSource){
        this.userRemoteDataSource = userRemoteDataSource;
        this.userDataRemoteDataSource = userDataRemoteDataSource;
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
        Result.UserResponseSuccess result = new Result.UserResponseSuccess(null);
        userMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessDeleteUser(User user) {
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
