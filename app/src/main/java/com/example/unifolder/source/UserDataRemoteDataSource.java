package com.example.unifolder.source;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.unifolder.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserDataRemoteDataSource extends BaseUserDataRemoteDataSource{

    private static final String TAG = UserDataRemoteDataSource.class.getSimpleName();

    private final DatabaseReference databaseReference;

    public UserDataRemoteDataSource() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(FIREBASE_REALTIME_DATABASE);
        databaseReference = firebaseDatabase.getReference().getRef();
    }


    @Override
    public void saveUserData(User user) {
        databaseReference.child(FIREBASE_USERS_COLLECTION).child(user.getId_token()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Log.d(TAG, "User already present in Firebase Realtime Database");

                    userResponseCallback.onSuccessFromRemoteDatabase(user);
                } else {
                    Log.d(TAG, "User not present in Firebase Realtime Database");

                    databaseReference.child(FIREBASE_USERS_COLLECTION).child(user.getId_token()).setValue(user)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    userResponseCallback.onSuccessFromRemoteDatabase(user);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    userResponseCallback.onFailureFromRemoteDatabase(e.getLocalizedMessage());
                                }
                            });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                userResponseCallback.onFailureFromRemoteDatabase(error.getMessage());
            }
        });
    }

    @Override
    public void deleteUserRealtime(User user) {

        databaseReference.child(FIREBASE_USERS_COLLECTION).child(user.getId_token())
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Utente eliminato con successo
                            Log.d(TAG, "User deleted from the database");
                            userResponseCallback.onSuccessDeleteUserRealtime();
                        } else {
                            // Errore durante l'eliminazione dell'utente
                            Log.e(TAG, "Error deleting user from the database", task.getException());
                            userResponseCallback.onFailureFromRemoteDatabase(task.getException().getLocalizedMessage());
                        }
                    }
                });
    }

    public void getUserRealtime(User user)
    {
        databaseReference.child(FIREBASE_USERS_COLLECTION).child(user.getId_token()).get().addOnCompleteListener(task -> {
            if(!task.isSuccessful())
            {
                Log.d(TAG, "error getting data", task.getException());
                userResponseCallback.onFailureFromRemoteDatabase(task.getException().getLocalizedMessage());
            } else {
                User userRealtime = task.getResult().getValue(User.class);
                userResponseCallback.onSuccessFromRemoteDatabase(userRealtime);
            }
        });
    }

    public void setUserAvatar(User user, int selectedImage){
        databaseReference.child(FIREBASE_USERS_COLLECTION).child(user.getId_token()).child("id_avatar").setValue(selectedImage)
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.e(TAG, "Errore nell'aggiornamento di id_avatar", task.getException());
                        userResponseCallback.onFailureFromRemoteDatabase(task.getException().getLocalizedMessage());
                    } else {
                        Log.d(TAG, "id_avatar aggiornato con successo");
                        Log.d(TAG, "id aggiornato"+ user.getId_token());
                        userResponseCallback.onSuccessSetAvatar(user);
                    }
                });
    }
    }

