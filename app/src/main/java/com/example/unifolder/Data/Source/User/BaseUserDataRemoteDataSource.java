package com.example.unifolder.Data.Source.User;

import com.example.unifolder.Data.Repository.User.UserResponseCallback;
import com.example.unifolder.Model.User;

public abstract class BaseUserDataRemoteDataSource {
    protected UserResponseCallback userResponseCallback;
    public void setUserResponseCallback(UserResponseCallback userResponseCallback) {
        this.userResponseCallback = userResponseCallback;
    }
    public abstract void getUserRealtime(User user);
    public abstract void saveUserData(User user);
    public abstract void deleteUserRealtime(User user);
    public abstract void setUserAvatar(User user, int selectedImage);

}
