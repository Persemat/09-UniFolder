package com.example.unifolder.Ui.Main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.unifolder.Adapter.AvatarAdapter;
import com.example.unifolder.Data.Repository.User.IUserRepository;
import com.example.unifolder.Model.Result;
import com.example.unifolder.Model.User;
import com.example.unifolder.R;
import com.example.unifolder.Util.ServiceLocator;
import com.example.unifolder.Ui.Welcome.UserViewModel;
import com.example.unifolder.Ui.Welcome.UserViewModelFactory;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    private final static String TAG = ProfileFragment.class.getSimpleName();
    private NavController navController;
    private TextView email;
    private ImageView avatar;
    private TextView first_name, last_name, username;
    private UserViewModel userViewModel;
    private SwitchMaterial themeToggleButton; private TextView colorMode;
    private SharedPreferences sharedPreferences;  private SharedPreferences.Editor editor;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        IUserRepository userRepository = ServiceLocator.getInstance().
                getUserRepository(requireActivity().getApplication());
        userViewModel = new ViewModelProvider(
                requireActivity(),
                new UserViewModelFactory(userRepository)).get(UserViewModel.class);

        Button logoutButton = view.findViewById(R.id.logout_button);
        Button deleteButton = view.findViewById(R.id.delete_account_button);
        themeToggleButton = view.findViewById(R.id.themeToggleButton);
        colorMode = view.findViewById(R.id.darkMode);

        sharedPreferences = requireActivity().getSharedPreferences("MODE", Context.MODE_PRIVATE);
        boolean darkMode = sharedPreferences.getBoolean("darkMode",false);

        first_name = view.findViewById(R.id.user_firstname);
        last_name = view.findViewById(R.id.user_lastname);
        username = view.findViewById(R.id.user_username);
        avatar = view.findViewById(R.id.avatar_image);
        email = view.findViewById(R.id.user_email);

        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            themeToggleButton.setChecked(false);
            colorMode.setText(requireContext().getString(R.string.dark_mode));
        } else {
            themeToggleButton.setChecked(true);
            colorMode.setText(requireContext().getString(R.string.light_mode));
        }
        themeToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Attiva il tema chiaro
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    colorMode.setText(requireContext().getString(R.string.light_mode));
                    editor = sharedPreferences.edit();
                    editor.putBoolean("darkMode", false);
                } else {
                    // Attiva il tema scuro
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    colorMode.setText(requireContext().getString(R.string.dark_mode));
                    editor = sharedPreferences.edit();
                    editor.putBoolean("darkMode", true );
                }
                editor.apply();
            }
        });

        userViewModel.getUserMutableLiveData(userViewModel.getLoggedUser()).observe(
                getViewLifecycleOwner(), result -> {
                    if (result.isSuccess()) {
                        User user = ((Result.UserResponseSuccess) result).getData();
                        if (user != null) {
                            first_name.setText(user.getFirstName());
                            last_name.setText(user.getLastName());
                            email.setText(user.getEmail());
                            username.setText("@" + user.getUsername());

                            int resourceId = user.getId_avatar();
                            Context context = view.getContext();
                            Log.d(TAG,"resource id: " + resourceId);
                            Drawable drawable = ContextCompat.getDrawable(context, resourceId);
                            avatar.setImageDrawable(drawable);

                        }
                    } else {
                        Snackbar.make(requireActivity().findViewById(android.R.id.content),
                                getErrorMessage(((Result.Error) result).getMessage()),
                                Snackbar.LENGTH_SHORT).show();
                    }
                });

        // GESTIONE DEL CLICK DEL PULSANTE LOGOUT
        logoutButton.setOnClickListener(v -> {
            userViewModel.logout().observe(getViewLifecycleOwner(), result -> {
                if (result.isSuccess()) {
                    Intent intent = new Intent(requireContext(), MainActivity.class);
                    startActivity(intent);
                    requireActivity().finish();
                } else {
                    Snackbar.make(view,
                            requireActivity().getString(R.string.unexpected_error),
                            Snackbar.LENGTH_SHORT).show();
                }
            });
        });

        //GESTIONE PUSANTE CANCELLAZIONE ACCOUNT
        deleteButton.setOnClickListener(v ->{new MaterialAlertDialogBuilder(requireContext(),R.style.CustomDialogTheme)
                .setTitle(R.string.delete_account_question)
                .setMessage(R.string.delete_account_message)
                .setPositiveButton(R.string.confirm, (dialog, which) -> {

                    userViewModel.deleteAccount().observe(getViewLifecycleOwner(), result -> {
                        if (result.isSuccess()) {

                            Intent intent = new Intent(requireContext(), MainActivity.class);
                            startActivity(intent);

                            // Passa all'attività di destinazione e al fragment specifico
                            NavController navController = Navigation.findNavController(requireActivity(), R.id.login_nav_graph);
                            navController.navigate(R.id.registrationFragment);

                            requireActivity().finish();

                        } else {
                            Snackbar.make(view,
                                    requireActivity().getString(R.string.unexpected_error),
                                    Snackbar.LENGTH_SHORT).show();
                        }
                    });
                    dialog.dismiss(); // Chiudi il dialog
                })
                .setNegativeButton(R.string.close, (dialog, which) -> {
                    // Azione da eseguire quando l'utente preme "Annulla"
                    dialog.dismiss(); // Chiudi il dialog
                })
                .show();

        });

        avatar.setOnClickListener(v -> showPopup(v));
    }

    private void showPopup(View anchorView) {

        // Infla il layout del pop up
        View popupView = LayoutInflater.from(anchorView.getContext()).inflate(R.layout.grid_view_avatar, null);
        // Creazione del pop up
        PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        // Inizializzazione del gridView nel popupView
        GridView gridView = popupView.findViewById(R.id.grid_view_avatar);
        // Creazione e impostazione dell'adapter per il gridView
        AvatarAdapter adapter = new AvatarAdapter(anchorView.getContext());
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // Ottieni l'ID dell'immagine cliccata
                int selectedImageId = (int) view.getTag();
                Log.d(TAG, "Selected Image ID: " + selectedImageId);
                userViewModel.setUserAvatar(userViewModel.getLoggedUser(), selectedImageId).observe(
                        getViewLifecycleOwner(), result -> {
                            if (result.isSuccess()) {
                                //User user = ((Result.UserResponseSuccess) result).getData();
                                Context context = view.getContext();
                                Drawable drawable = ContextCompat.getDrawable(context, selectedImageId);
                                avatar.setImageDrawable(drawable);
                                popupWindow.dismiss();
                            } else {
                                Snackbar.make(requireActivity().findViewById(android.R.id.content),
                                        getErrorMessage(((Result.Error) result).getMessage()),
                                        Snackbar.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        // animazione di entrata se lo desideri
        popupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
        // Visualizzazione del popup nella posizione desiderata, ad esempio, al centro dell'ancora
        popupWindow.showAtLocation(anchorView, Gravity.CENTER, 0, 0);
    }


    private String getErrorMessage(String errorType) {
        return errorType;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Salva lo stato del ToggleButton
        outState.putBoolean("themeChecked", themeToggleButton.isChecked());
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        // Ripristina lo stato del ToggleButton
        if(savedInstanceState != null) {
            boolean isChecked = savedInstanceState.getBoolean("themeChecked");
            themeToggleButton.setChecked(isChecked);
        }
    }
}