package com.example.cabinetprivat;

import android.annotation.SuppressLint; // Această adnotare nu mai este necesară dacă ștergi onBackPressed()
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HelloActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    // Firebase Authentication
    private FirebaseAuth mAuth;

    // UI elements from Nav Header
    private TextView navHeaderName;
    private TextView navHeaderEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello);

        // Inițializare Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        // Inițializare UI
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        // Configurare Navigation Drawer
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Referințe la elementele din header-ul Navigation Drawer
        View headerView = navigationView.getHeaderView(0);
        navHeaderName = headerView.findViewById(R.id.textView_nav_header_name);
        navHeaderEmail = headerView.findViewById(R.id.textView_nav_header_email);

        // Populează header-ul cu datele utilizatorului la pornire
        updateNavHeader();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reîmprospătează datele în header dacă te întorci la această activitate
        updateNavHeader();
    }

    // Metodă pentru popularea header-ului Navigation Drawer cu datele utilizatorului
    private void updateNavHeader() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // Utilizatorul nu este logat, afișează date generice
            navHeaderName.setText("Welcome!");
            navHeaderEmail.setText("Not Logged In");
            return;
        }

        // Preia numele și emailul direct din Firebase Authentication
        String displayName = currentUser.getDisplayName();
        String email = currentUser.getEmail();

        navHeaderName.setText(displayName != null && !displayName.isEmpty() ? displayName : "Guest User");
        navHeaderEmail.setText(email != null && !email.isEmpty() ? email : "No Email");
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);

        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent intent = new Intent(HelloActivity.this, HomeActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_appointments) {
            Intent intent = new Intent(this, AppointmentsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_profile) {
            Intent intent = new Intent(this, PatientProfileActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
            mAuth.signOut();
            Toast.makeText(this, "Logged out successfully.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, OnboardingActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }

        return true;
    }

    // Am șters metoda onBackPressed() de aici.
    // Comportamentul implicit al butonului de back va fi utilizat.
}

