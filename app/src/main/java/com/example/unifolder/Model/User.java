package com.example.unifolder.Model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user")
public class User {
    @PrimaryKey
    @NonNull
    private String email;
    private String firstName;
    private String lastName;
    private String username;
    private String id_token;
    private int id_avatar;

    public User() {
    }

    public User(String firstName,String lastName, String username ,@NonNull String email, int id_avatar, String id_token) {
        this.firstName=firstName;
        this.lastName=lastName;
        this.username=username;
        this.email=email;
        this.id_avatar = id_avatar;
        this.id_token=id_token;
    }
    public User(@NonNull String email, String id_token) {
        this.email=email;
        this.id_token=id_token;
    }

    public User(String idToken) {
        this.id_token = idToken;
    }

    public int getId_avatar() {
        return id_avatar;
    }

    public void setId_avatar(int id_avatar) {
        this.id_avatar = id_avatar;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getId_token() {
        return id_token;
    }

    public void setId_token(String id_token) {
        this.id_token = id_token;
    }

    @NonNull
    public String getEmail() {
        return email;
    }

    public void setEmail(@NonNull String email) {
        this.email = email;
    }
}

