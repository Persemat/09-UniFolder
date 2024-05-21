package com.example.unifolder.Ui.Welcome;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.unifolder.Data.Repository.User.IUserRepository;
import com.example.unifolder.Model.Result;
import com.example.unifolder.Model.User;

public class UserViewModel extends ViewModel {
    private final IUserRepository userRepository;
    private MutableLiveData<Result> userMutableLiveData;
    private boolean authenticationError;


    //COSTRUTTORE
    public UserViewModel(IUserRepository userRepository) {
        this.userRepository = userRepository;
        authenticationError = false;
    }

    public UserViewModel() {
        this.userRepository = null;
    }

    public MutableLiveData<Result> getUserMutableLiveData(String email, String password, boolean isUserRegistered) {
        if (userMutableLiveData == null) {
            getUserData(email, password, isUserRegistered);
        }
        return userMutableLiveData;
    }

    public MutableLiveData<Result> getUserMutableLiveData() {
        if (userMutableLiveData == null) {
            // Inizializza userMutableLiveData se non è stato ancora inizializzato
            userMutableLiveData = new MutableLiveData<>();
        }
        User loggedUser = userRepository.getLoggedUser();
        if(loggedUser != null)
            userMutableLiveData = (userRepository.getUserData(loggedUser));
        return userMutableLiveData;
    }



    public MutableLiveData<Result> getUserMutableLiveData(String firstName, String lastName, String username, String email, String password, int id_avatar, boolean isUserRegistered) {
        if (userMutableLiveData == null) {
            getUserData(firstName, lastName, email, username, password, id_avatar, isUserRegistered);
        }
        return userMutableLiveData;
    }
    public MutableLiveData<Result> getUserMutableLiveData(User user) {
        if (userMutableLiveData == null) {
            getUserData(user);
        }
        return userMutableLiveData;
    }

    private void getUserData(String email, String password, boolean isUserRegistered) {
        userMutableLiveData = userRepository.getUser(email, password, isUserRegistered);
    }
    private void getUserData(String firstName, String lastName, String username, String email, String password, int id_avatar, boolean isUserRegistered) {
        userMutableLiveData = userRepository.getUser(firstName, lastName, username, email, password, id_avatar, isUserRegistered);
    }
    private void getUserData(User user) {
        userMutableLiveData = userRepository.getUserData(user);
    }
    public void getUser(String email, String password, boolean isUserRegistered) {
        userRepository.getUser(email, password, isUserRegistered);
    }

    public MutableLiveData<Result> logout() {
        if (userMutableLiveData == null) {
            userMutableLiveData = userRepository.logout();
        } else {
            userRepository.logout();
        }

        return userMutableLiveData;
    }

    public MutableLiveData<Result> deleteAccount() {
        if (userMutableLiveData == null) {
            userMutableLiveData = userRepository.deleteAccount();
        } else {
            userRepository.deleteAccount();
        }

        return userMutableLiveData;
    }

    public MutableLiveData<Result> setUserAvatar(User user, int selectedImage){
        userRepository.setUserAvatar(user, selectedImage);
        return userMutableLiveData;
    }

    public void resetPassword(String email)
    {
        userRepository.resetPassword(email);
    }

    public User getLoggedUser() {
        return userRepository.getLoggedUser();
    }

    public boolean isAuthenticationError() {
        return authenticationError;
    }
    public void setAuthenticationError(boolean authenticationError) {
        this.authenticationError = authenticationError;
    }
}
