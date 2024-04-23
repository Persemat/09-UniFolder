package com.example.unifolder.Welcome;

import static com.example.unifolder.Util.Costants.INVALID_CREDENTIALS_ERROR;
import static com.example.unifolder.Util.Costants.INVALID_USER_ERROR;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.unifolder.MainActivity;
import com.example.unifolder.R;
import com.example.unifolder.Data.User.IUserRepository;
import com.example.unifolder.Model.Result;
import com.example.unifolder.Util.ServiceLocator;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.apache.commons.validator.routines.EmailValidator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private NavController navController;
    private TextInputLayout emailTextInputLayout;
    private TextInputLayout passwordTextInputLayout;
    Button signUpButton;
    Button loginButton;
    Button forgotPasswordButton;
    UserViewModel userViewModel;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
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

        navController = Navigation.findNavController(requireActivity(), R.id.container_login_activity);
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // Trova il bottone arrow_back
        ImageButton backButton = view.findViewById(R.id.arrow_back);

        // Aggiungi un listener per il clic del bottone arrow_back
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Naviga alla main activity quando si preme la freccia indietro
                Intent intent = new Intent(requireContext(), MainActivity.class);
                startActivity(intent);
                requireActivity().finish(); // Chiude l'activity corrente (login activity)
            }
        });

        // Inizializzazione di userViewModel
        IUserRepository userRepository = ServiceLocator.getInstance().getUserRepository(requireActivity().getApplication());
        userViewModel = new ViewModelProvider(
                this,
                new UserViewModelFactory(userRepository)).get(UserViewModel.class);

        emailTextInputLayout = view.findViewById(R.id.text_input_layout_email);
        passwordTextInputLayout = view.findViewById(R.id.text_input_layout_password);
        signUpButton = view.findViewById(R.id.button_sign_up);
        loginButton = view.findViewById(R.id.button_login);
        forgotPasswordButton = view.findViewById(R.id.button_forgot_password);


        // GESTIONE CLICK PASSWORD DIMENTICATA

        forgotPasswordButton.setOnClickListener(item -> {
            String email = emailTextInputLayout.getEditText().getText().toString();
            if (isEmailCorrect(email)) {
                userViewModel.resetPassword(email);
                Snackbar.make(
                        requireActivity().findViewById(android.R.id.content),
                        getString(R.string.email_sent),
                        Snackbar.LENGTH_SHORT).show();
            }

        });

        // CLICCANDO SU IL PULSANTE DI SIGN UP SI PASSA ALLA SCHERMATA DI SIGN UP
        signUpButton.setOnClickListener(item -> {
            navController.navigate(R.id.registrationFragment);
        });

        // INIZIO GESTIONE PULSANTE DI LOGIN
        loginButton.setOnClickListener(item -> {
            // Log.d(TAG, "Button Clicked");
            String email = emailTextInputLayout.getEditText().getText().toString();
            String password = passwordTextInputLayout.getEditText().getText().toString();

            if (isEmailCorrect(email) && isPasswordCorrect(password)) {
                if (!userViewModel.isAuthenticationError()) {
                    userViewModel.getUserMutableLiveData(email, password, true).observe(
                            getViewLifecycleOwner(), result -> {
                                if (result.isSuccess()) {
                                    userViewModel.setAuthenticationError(false);
                                    Intent intent = new Intent(requireContext(), MainActivity.class);
                                    startActivity(intent);
                                    requireActivity().finish();
                                } else {
                                    userViewModel.setAuthenticationError(true);
                                    Snackbar.make(requireActivity().findViewById(android.R.id.content),
                                            getErrorMessage(((Result.Error) result).getMessage()),
                                            Snackbar.LENGTH_SHORT).show();
                                }
                            });

                } else {
                    userViewModel.getUser(email, password, true);
                }
            } else {
                Snackbar.make(
                        requireActivity().findViewById(android.R.id.content),
                        getString(R.string.error_data),
                        Snackbar.LENGTH_SHORT).show();
            }
        }); // FINE GESTIONE PULSANTE LOGIN

        return view;
    }

    private String getErrorMessage (String errorType){
        switch (errorType) {
            case INVALID_CREDENTIALS_ERROR:
                return getString(R.string.error_login_password_message);
            case INVALID_USER_ERROR:
                return getString(R.string.error_login_user_message);
            default:
                return getString(R.string.unexpected_error);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            navController.navigate(R.id.mainActivity);
        }
    }

    // FUNZIONI PER VERIFICA DELLA CORREZIONE MAIL E PASSWORD
    private boolean isEmailCorrect (String email){
        boolean result = EmailValidator.getInstance().isValid(email);
        if (!result) {
            emailTextInputLayout.setError("Email is not correct");
        } else {
            emailTextInputLayout.setError(null);
        }
        return result;
    }

    private boolean isPasswordCorrect (String password){
       boolean result = password != null && password.length() >= 6;
        if (!result) {
            passwordTextInputLayout.setError("Password is not correct");
        } else {
            passwordTextInputLayout.setError(null);
        }
        return result;
    }

    private void startActivityBasedOnCondition(Class<?> destinationActivity, int destination) {
            Navigation.findNavController(requireView()).navigate(destination);
        requireActivity().finish();
    }

}