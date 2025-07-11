package com.example.cabinetprivat;

import android.app.Activity; // Poți folosi androidx.appcompat.app.AppCompatActivity pentru mai multă compatibilitate
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper; // Importă Looper pentru Handler

import com.google.firebase.auth.FirebaseAuth; // Importă FirebaseAuth
import com.google.firebase.auth.FirebaseUser; // Importă FirebaseUser

public class SplashActivity extends Activity { // Poți schimba în AppCompatActivity dacă vrei mai multe funcționalități

    private FirebaseAuth auth; // Declară instanța FirebaseAuth

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash); // Asigură-te că ai acest layout

        // Inițializare Firebase Authentication
        auth = FirebaseAuth.getInstance();

        // Folosim un Handler pentru a crea o întârziere
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            FirebaseUser currentUser = auth.getCurrentUser(); // Obține utilizatorul curent

            Intent intent;
            if (currentUser != null) {
                // Utilizatorul este deja logat, mergi la HelloActivity
                intent = new Intent(SplashActivity.this, HelloActivity.class);
            } else {
                // Niciun utilizator logat, mergi la AuthActivity
                // Dacă AuthActivity este de fapt OnboardingActivity, folosește OnboardingActivity.class
                intent = new Intent(SplashActivity.this, OnboardingActivity.class); // Sau OnboardingActivity.class
            }
            startActivity(intent);
            finish(); // Închide SplashActivity pentru a nu putea reveni la ea cu butonul de "back"
        }, 3000); // Întârziere de 3 secunde (3000 milisecunde). Poți ajusta timpul.
    }
}
