package com.example.unifolder;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

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

        // Aggiungi il listener per il cambio destinazione
        navController.addOnDestinationChangedListener(destinationChangedListener);
    }

    // Listener per il cambio destinazione
    NavController.OnDestinationChangedListener destinationChangedListener = new NavController.OnDestinationChangedListener(){
        @Override
        public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
            // Controlla se l'utente non è autenticato e la destinazione richiede il login
            if (!isUserLoggedIn() && destinationRequiresLogin(destination)) {
                // Reindirizza alla schermata di login
                navigateToLoginScreen();
            }
        }
    };

    // Metodo per verificare se l'utente è autenticato
    private boolean isUserLoggedIn() {
        // Inserisci qui la logica per verificare se l'utente è autenticato
        // Ritorna true se l'utente è autenticato, altrimenti false
        return false; // Per esempio, ritorna sempre false per simulare l'utente non autenticato
    }

    // Metodo per controllare se la destinazione richiede il login
    private boolean destinationRequiresLogin(NavDestination destination) {
        return destination.getId() != R.id.homeFragment; // Cambia questa condizione a seconda delle tue esigenze
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Controlla se l'utente non è autenticato quando l'app viene avviata e lo reindirizza alla schermata di login
        if (!isUserLoggedIn()) {
            navigateToLoginScreen();
        }
    }

    // Metodo per reindirizzare alla schermata di login
    private void navigateToLoginScreen() {
      navController.navigate(R.id.loginFragment);
         // Opzionale: chiudi l'attuale attività per impedire al utente di tornare indietro senza effettuare il login
    }

    // Assicurati di rimuovere il listener quando l'attività viene distrutta per evitare memory leaks
    @Override
    protected void onDestroy() {
        super.onDestroy();
        navController.removeOnDestinationChangedListener(destinationChangedListener);
    }
}
