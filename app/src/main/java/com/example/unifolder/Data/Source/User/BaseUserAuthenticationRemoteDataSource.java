package com.example.unifolder.Data.Source.User;


import com.example.unifolder.Data.Repository.User.UserResponseCallback;
import com.example.unifolder.Model.User;

public abstract class BaseUserAuthenticationRemoteDataSource {

    protected UserResponseCallback userResponseCallback;

    public void setUserResponseCallback(UserResponseCallback userResponseCallback) {
        this.userResponseCallback = userResponseCallback;
    }
    public abstract User getLoggedUser();
    public abstract void logout();
    public abstract void deleteAccount();
    public abstract void sendEmailPasswordReset(String email);
    public abstract void signUp(String firstName, String lastName, String Username, String email, int id_avatar, String password);
    public abstract void signIn(String email, String password);
}

