package com.example.cabinetprivat;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class PatientProfileActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_profile); // asigură-te că ai acest layout

        bottomNavigationView = findViewById(R.id.bottom_nav);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull android.view.MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_home) {
                    Toast.makeText(PatientProfileActivity.this, "Acasă", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (id == R.id.nav_appointments) {
                    Toast.makeText(PatientProfileActivity.this, "Programări", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (id == R.id.nav_profile) {
                    Toast.makeText(PatientProfileActivity.this, "Profil", Toast.LENGTH_SHORT).show();
                    return true;
                }

                return false;
            }
        });
    }
}





