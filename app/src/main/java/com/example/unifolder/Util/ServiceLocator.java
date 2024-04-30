package com.example.unifolder.Util;

import android.app.Application;
import android.content.Context;

import com.example.unifolder.Source.Document.DocumentLocalDataSource;
import com.example.unifolder.Source.Document.DocumentRemoteDataSource;
import com.example.unifolder.Data.User.IUserRepository;
import com.example.unifolder.Data.User.UserRepository;
import com.example.unifolder.Source.User.BaseUserAuthenticationRemoteDataSource;
import com.example.unifolder.Source.User.BaseUserDataRemoteDataSource;
import com.example.unifolder.Source.User.UserAuthenticationRemoteDataSource;
import com.example.unifolder.Source.User.UserDataRemoteDataSource;

public class ServiceLocator {
    private static volatile ServiceLocator INSTANCE = null;
    private static Application application;
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