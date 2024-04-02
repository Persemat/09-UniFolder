package com.example.unifolder.Util;

import android.app.Application;

import com.example.unifolder.Data.User.IUserRepository;
import com.example.unifolder.Data.User.UserRepository;
import com.example.unifolder.Source.User.BaseUserAuthenticationRemoteDataSource;
import com.example.unifolder.Source.User.BaseUserDataRemoteDataSource;
import com.example.unifolder.Source.User.UserAuthenticationRemoteDataSource;
import com.example.unifolder.Source.User.UserDataRemoteDataSource;

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