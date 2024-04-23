package com.example.unifolder;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.unifolder.Welcome.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private NavController navController;
    private AppBarConfiguration appBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Menu bar setup
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container_view);
        navController = navHostFragment.getNavController();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.homeFragment, R.id.uploadFragment, R.id.profileFragment).build();

        NavigationUI.setupWithNavController(bottomNavigationView, navController);


        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
        // Aggiungi un listener ai pulsanti della bottom bar
        bottomNavigationView.setOnItemSelectedListener(item -> {
                // Naviga alla LoginActivity ogni volta che viene cliccato un pulsante della bottom bar
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish(); // Chiudi l'activity corrente (MainActivity)
                return false; // Ritorna false per indicare che l'evento non è stato consumato
            });
        }
    }

    // Metodo statico per disabilitare le animazioni dell'activity
    public void disableAnimations() {
        setWindowAnimationsEnabled(false);
    }

    private void setWindowAnimationsEnabled(boolean enabled) {
        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            if (enabled) {
                layoutParams.windowAnimations = android.R.style.Animation_Activity;
            } else {
                layoutParams.windowAnimations = 0; // Disabilita le animazioni
            }
            window.setAttributes(layoutParams);
        }
    }

}
