package com.example.unifolder.Data.User;

import com.example.unifolder.Model.User;

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

