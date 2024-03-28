package com.example.unifolder.Welcome;

import static com.example.unifolder.util.Costants.USER_COLLISION_ERROR;
import static com.example.unifolder.util.Costants.WEAK_PASSWORD_ERROR;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.unifolder.MainActivity;
import com.example.unifolder.R;
import com.example.unifolder.data.user.IUserRepository;
import com.example.unifolder.model.Result;
import com.example.unifolder.model.User;
import com.example.unifolder.util.ServiceLocator;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import org.apache.commons.validator.routines.EmailValidator;

public class Registration extends AppCompatActivity {

    private static final String TAG = Registration.class.getSimpleName();
    private TextInputLayout firstNameTextInputLayout;
    private TextInputLayout lastNameTextInputLayout;
    private TextInputLayout emailTextInputLayout;
    private TextInputLayout newPasswordTextInputLayout;
    private TextInputLayout confirmPasswordTextInputLayout;

    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_registration);

        // Inizializzazione di userViewModel
        IUserRepository userRepository = ServiceLocator.getInstance().
                getUserRepository(this.getApplication());
        userViewModel = new ViewModelProvider(
                this,
                new UserViewModelFactory(userRepository)).get(UserViewModel.class);
        userViewModel.setAuthenticationError(false);

        firstNameTextInputLayout = findViewById(R.id.text_input_layout_first_name);
        lastNameTextInputLayout = findViewById(R.id.text_input_layout_last_name);
        emailTextInputLayout = findViewById(R.id.text_input_layout_email);
        newPasswordTextInputLayout = findViewById(R.id.text_input_layout_new_password);
        confirmPasswordTextInputLayout = findViewById(R.id.text_input_layout_confirm_password);

        // GESTISCE IL CLICK DEL PULSANTE LOGIN
        Button loginButton = findViewById(R.id.button_login);
        loginButton.setOnClickListener(item -> {
            Intent login = new Intent(getApplicationContext(), Login.class);
            startActivity(login);
            finish();
        });

        // GESTISCE IL CLICK DEL PULSANTE SIGN UP
        Button signUpButton = findViewById(R.id.button_sign_up);
        signUpButton.setOnClickListener(item -> {
            // Log.d(TAG, "Button Clicked");
            String first_name = firstNameTextInputLayout.getEditText().getText().toString();
            String last_name = lastNameTextInputLayout.getEditText().getText().toString();
            String email = emailTextInputLayout.getEditText().getText().toString();
            String new_password = newPasswordTextInputLayout.getEditText().getText().toString();
            String confirm_password = confirmPasswordTextInputLayout.getEditText().getText().toString();
            int idImageAvatar = R.drawable.baseline_account_circle_24;
            Log.d(TAG, "Id Avatar: " + idImageAvatar);

            if (isNameCorrect(first_name) && isLastNameCorrect(last_name)
                    && isEmailCorrect(email) && isPasswordCorrect(new_password, confirm_password)) {
                if (!userViewModel.isAuthenticationError()) {
                    userViewModel.getUserMutableLiveData(first_name, last_name, email, new_password, idImageAvatar, false).observe(
                            this, result -> {
                                if (result.isSuccess()) {
                                    User user = ((Result.UserResponseSuccess) result).getData();
                                    Log.d(TAG, user.toString());

                                    userViewModel.setAuthenticationError(false);
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    userViewModel.setAuthenticationError(true);
                                    Snackbar.make(findViewById(android.R.id.content),
                                            getErrorMessage(((Result.Error) result).getMessage()),
                                            Snackbar.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    userViewModel.getUser(email, new_password, false);
                }
            }
            else {
                userViewModel.setAuthenticationError(true);
                Snackbar.make(
                        findViewById(android.R.id.content),
                        getString(R.string.error_data),
                        Snackbar.LENGTH_SHORT).show();
            }
        });
        // FINE GESTIONE PULSANTE SIGN UP

    }
    private String getErrorMessage(String message) {
        switch(message) {
            case WEAK_PASSWORD_ERROR:
                return getString(R.string.error_password);
            case USER_COLLISION_ERROR:
                return getString(R.string.error_user_collision_message);
            default:
                return getString(R.string.unexpected_error);
        }
    }

    private boolean isNameCorrect(String name) {
        boolean result = name.length()>0;
        if (!result) {
            firstNameTextInputLayout.setError("First name is not correct");
        } else {
            firstNameTextInputLayout.setError(null);
        }
        return result;
    }
    private boolean isLastNameCorrect(String lastName) {
        boolean result = lastName.length()>0;
        if (!result) {
            lastNameTextInputLayout.setError("Last name is not correct");
        } else {
            lastNameTextInputLayout.setError(null);
        }
        return result;
    }
    private boolean isEmailCorrect(String email) {
        boolean result = EmailValidator.getInstance().isValid(email);
        if (!result) {
            emailTextInputLayout.setError("Email is not correct");
        } else {
            emailTextInputLayout.setError(null);
        }
        return result;
    }
    private boolean isPasswordCorrect(String new_password, String confirm_password) {
        boolean result = new_password != null && new_password.length() >= 8;
        if (!result) {
            newPasswordTextInputLayout.setError("Password is not correct");
            confirmPasswordTextInputLayout.setError("");
        } else {
            newPasswordTextInputLayout.setError(null);
        }
        if (!(confirm_password.equals(new_password))) {
            confirmPasswordTextInputLayout.setError("Passwords are not equals");
            result = false;
        } else {
            confirmPasswordTextInputLayout.setError(null);
        }
        return result;
    }
}
