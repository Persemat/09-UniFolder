package com.example.unifolder.util;

import android.app.Application;

import com.example.unifolder.data.user.IUserRepository;
import com.example.unifolder.data.user.UserRepository;
import com.example.unifolder.source.BaseUserAuthenticationRemoteDataSource;
import com.example.unifolder.source.BaseUserDataRemoteDataSource;
import com.example.unifolder.source.UserAuthenticationRemoteDataSource;
import com.example.unifolder.source.UserDataRemoteDataSource;

public class ServiceLocator {
    private static volatile ServiceLocator INSTANCE = null;

    private ServiceLocator() {
    }

    public static ServiceLocator getInstance() {
        if (INSTANCE == null) {
            synchronized (ServiceLocator.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ServiceLocator();
                }
            }
        }
        return INSTANCE;
    }

    public IUserRepository getUserRepository(Application application) {


        BaseUserAuthenticationRemoteDataSource userRemoteAuthenticationDataSource = new UserAuthenticationRemoteDataSource();
        BaseUserDataRemoteDataSource userDataRemoteDataSource = new UserDataRemoteDataSource();

        return new UserRepository(userRemoteAuthenticationDataSource, userDataRemoteDataSource);
    }

}