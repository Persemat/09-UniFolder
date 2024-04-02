package com.example.unifolder.Welcome;

import static com.example.unifolder.Util.Costants.USER_COLLISION_ERROR;
import static com.example.unifolder.Util.Costants.WEAK_PASSWORD_ERROR;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.unifolder.R;
import com.example.unifolder.Data.User.IUserRepository;
import com.example.unifolder.Model.Result;
import com.example.unifolder.Model.User;
import com.example.unifolder.Util.ServiceLocator;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import org.apache.commons.validator.routines.EmailValidator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegistrationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegistrationFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static final String TAG = RegistrationFragment.class.getSimpleName();
    private TextInputLayout firstNameTextInputLayout;
    private TextInputLayout lastNameTextInputLayout;
    private TextInputLayout emailTextInputLayout;
    private TextInputLayout newPasswordTextInputLayout;
    private TextInputLayout confirmPasswordTextInputLayout;
    private NavController navController;

    private UserViewModel userViewModel;

    public RegistrationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegistrationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegistrationFragment newInstance(String param1, String param2) {
        RegistrationFragment fragment = new RegistrationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        navController = Navigation.findNavController(requireActivity(), R.id.fragment_container_view);
        View view = inflater.inflate(R.layout.fragment_registration, container, false);

        // Inizializzazione di userViewModel
        IUserRepository userRepository = ServiceLocator.getInstance().
                getUserRepository(requireActivity().getApplication());
        userViewModel = new ViewModelProvider(
                this,
                new UserViewModelFactory(userRepository)).get(UserViewModel.class);
        userViewModel.setAuthenticationError(false);

        firstNameTextInputLayout = view.findViewById(R.id.text_input_layout_first_name);
        lastNameTextInputLayout = view.findViewById(R.id.text_input_layout_last_name);
        emailTextInputLayout = view.findViewById(R.id.text_input_layout_email);
        newPasswordTextInputLayout = view.findViewById(R.id.text_input_layout_new_password);
        confirmPasswordTextInputLayout = view.findViewById(R.id.text_input_layout_confirm_password);

        // GESTISCE IL CLICK DEL PULSANTE LOGIN
        Button loginButton = view.findViewById(R.id.button_login);
        loginButton.setOnClickListener(item -> {
            navController.navigate(R.id.loginFragment);
        });

        // GESTISCE IL CLICK DEL PULSANTE SIGN UP
        Button signUpButton = view.findViewById(R.id.button_sign_up);
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
                            getViewLifecycleOwner(), result -> {
                                if (result.isSuccess()) {
                                    User user = ((Result.UserResponseSuccess) result).getData();
                                    Log.d(TAG, user.toString());

                                    userViewModel.setAuthenticationError(false);
                                    navController.navigate(R.id.homeFragment);
                                } else {
                                    userViewModel.setAuthenticationError(true);
                                    Snackbar.make(view.findViewById(android.R.id.content),
                                            getErrorMessage(((Result.Error) result).getMessage()),
                                            Snackbar.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    userViewModel.getUser(email, new_password, false);
                }
            } else {
                userViewModel.setAuthenticationError(true);
                Snackbar.make(
                        view.findViewById(android.R.id.content),
                        getString(R.string.error_data),
                        Snackbar.LENGTH_SHORT).show();
            }
        });
        // FINE GESTIONE PULSANTE SIGN UP


        // Inflate the layout for this fragment
        return view;
    }

    private String getErrorMessage(String message) {
        switch (message) {
            case WEAK_PASSWORD_ERROR:
                return getString(R.string.error_password);
            case USER_COLLISION_ERROR:
                return getString(R.string.error_user_collision_message);
            default:
                return getString(R.string.unexpected_error);
        }
    }

    private boolean isNameCorrect(String name) {
        boolean result = name.length() > 0;
        if (!result) {
            firstNameTextInputLayout.setError("First name is not correct");
        } else {
            firstNameTextInputLayout.setError(null);
        }
        return result;
    }

    private boolean isLastNameCorrect(String lastName) {
        boolean result = lastName.length() > 0;
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