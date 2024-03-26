package com.example.unifolder.data.user;

import com.example.unifolder.model.User;

import java.util.List;
public interface UserResponseCallback {

    void onSuccessFromAuthentication(User user);
    void onFailureFromAuthentication(String message);
    void onSuccessFromRemoteDatabase(User user);

    void onFailureFromRemoteDatabase(String message);
    void onSuccessLogout();

    void onSuccessDeleteUser(User user);
    void onSuccessDeleteUserRealtime();

    void onSuccessSetAvatar(User user);
}

