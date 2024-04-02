package com.example.unifolder.util;

import android.app.Application;
import android.content.Context;

import com.example.unifolder.DocumentLocalDataSource;
import com.example.unifolder.DocumentRemoteDataSource;
import com.example.unifolder.data.user.IUserRepository;
import com.example.unifolder.data.user.UserRepository;
import com.example.unifolder.source.BaseUserAuthenticationRemoteDataSource;
import com.example.unifolder.source.BaseUserDataRemoteDataSource;
import com.example.unifolder.source.UserAuthenticationRemoteDataSource;
import com.example.unifolder.source.UserDataRemoteDataSource;

public class ServiceLocator {
    private static volatile ServiceLocator INSTANCE = null;

    private static DocumentLocalDataSource localDataSource;
    private static DocumentRemoteDataSource remoteDataSource;

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

    // Metodo per ottenere un'istanza di LocalDataSource
    public DocumentLocalDataSource getLocalDataSource(Context context) {
        if (localDataSource == null) {
            localDataSource = new DocumentLocalDataSource(context.getApplicationContext());
        }
        return localDataSource;
    }

    // Metodo per ottenere un'istanza di RemoteDataSource
    public DocumentRemoteDataSource getRemoteDataSource() {
        if (remoteDataSource == null) {
            remoteDataSource = new DocumentRemoteDataSource();
        }
        return remoteDataSource;
    }


    public IUserRepository getUserRepository(Application application) {


        BaseUserAuthenticationRemoteDataSource userRemoteAuthenticationDataSource = new UserAuthenticationRemoteDataSource();
        BaseUserDataRemoteDataSource userDataRemoteDataSource = new UserDataRemoteDataSource();

        return new UserRepository(userRemoteAuthenticationDataSource, userDataRemoteDataSource);
    }

}