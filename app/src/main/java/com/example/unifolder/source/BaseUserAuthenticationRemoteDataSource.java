package com.example.unifolder.source;

import com.example.unifolder.model.User;

public abstract class BaseUserAuthenticationRemoteDataSource {

    protected UserResponseCallback userResponseCallback;

    public void setUserResponseCallback(UserResponseCallback userResponseCallback) {
        this.userResponseCallback = userResponseCallback;
    }
    public abstract User getLoggedUser();
    public abstract void logout();
    public abstract void deleteAccount();
    public abstract void sendEmailPasswordReset(String email);
    public abstract void signUp(String firstName, String lastName, String email, int id_avatar, String password);
    public abstract void signIn(String email, String password);
}

