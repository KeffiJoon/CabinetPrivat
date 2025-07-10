package com.example.cabinetprivat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View; // Import corect pentru android.view.View
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cabinetprivat.adapters.DoctorAdapter;
import com.example.cabinetprivat.models.Doctor;
import com.google.android.material.navigation.NavigationView;

// Adaugă importurile pentru Firebase Authentication
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "HomeActivity";

    private DrawerLayout drawerLayout;
    private RecyclerView recyclerView;
    private DoctorAdapter doctorAdapter;
    private List<Doctor> doctorsList;

    // Declarații pentru Firebase Authentication
    private FirebaseAuth mAuth;

    // Declarații pentru elementele din header-ul Navigation Drawer
    private TextView navHeaderName;
    private TextView navHeaderEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Inițializare Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        Toolbar toolbar = findViewById(R.id.home_toolbar);
        setSupportActionBar(toolbar);

        // Ascunde titlul implicit al ActionBar-ului (unde scria "CabinetPrivat")
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        if (toolbarTitle != null) {
            // Folosim o resursă string pentru text, conform bunelor practici Android
            toolbarTitle.setText("Medici"); // Asigură-te că ai acest string în strings.xml
        }

        // Inițializare DrawerLayout și NavigationView
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        // Setăm listener-ul pentru click-urile din meniul de navigare
        navigationView.setNavigationItemSelectedListener(this);

        // Configurăm ActionBarDrawerToggle pentru a deschide/închide drawer-ul
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Referințe la elementele din header-ul Navigation Drawer
        View headerView = navigationView.getHeaderView(0); // Ia primul header view (dacă ai mai multe)
        navHeaderName = headerView.findViewById(R.id.textView_nav_header_name);
        navHeaderEmail = headerView.findViewById(R.id.textView_nav_header_email);

        // Populează header-ul cu datele utilizatorului la pornire
        updateNavHeader();

        // Inițializare RecyclerView
        recyclerView = findViewById(R.id.recyclerView_doctors);
        if (recyclerView == null) {
            Log.e(TAG, "RecyclerView with ID recyclerView_doctors not found in activity_home.xml.");
            Toast.makeText(this, "Eroare de inițializare: RecyclerView-ul nu a fost găsit.", Toast.LENGTH_LONG).show();
            return;
        }

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        // Apelăm metoda pentru a inițializa și încărca datele doctorilor
        initializeDoctorsData();
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
            if (navHeaderName != null) navHeaderName.setText("Welcome!");
            if (navHeaderEmail != null) navHeaderEmail.setText("Not Logged In");
            return;
        }

        // Preia numele și emailul direct din Firebase Authentication
        String displayName = currentUser.getDisplayName();
        String email = currentUser.getEmail();

        if (navHeaderName != null) {
            navHeaderName.setText(displayName != null && !displayName.isEmpty() ? displayName : "Guest User");
        }
        if (navHeaderEmail != null) {
            navHeaderEmail.setText(email != null && !email.isEmpty() ? email : "No Email");
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);

        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Toast.makeText(this, "Home clicked", Toast.LENGTH_SHORT).show();
            // Nu faci nimic, ești deja acasă în HomeActivity
        } else if (id == R.id.nav_appointments) {
            Toast.makeText(this, "Appointments clicked", Toast.LENGTH_SHORT).show();
            // Exemplu: Start o altă activitate
            // startActivity(new Intent(this, AppointmentsActivity.class));
            // Asigură-te că ai o activitate AppointmentsActivity
        } else if (id == R.id.nav_profile) {
            Toast.makeText(this, "Profile clicked", Toast.LENGTH_SHORT).show();
            // Exemplu: Start o altă activitate
            // startActivity(new Intent(this, PatientProfileActivity.class));
            // Asigură-te că ai o activitate PatientProfileActivity
        } else if (id == R.id.nav_logout) {
            mAuth.signOut(); // Delogare Firebase
            Toast.makeText(this, "Logged out successfully.", Toast.LENGTH_SHORT).show();
            // Redirecționare către o activitate de login/onboarding
            Intent intent = new Intent(this, OnboardingActivity.class); // Exemplu: OnboardingActivity sau LoginActivity
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish(); // Închide HomeActivity pentru a preveni revenirea cu butonul de back
        }

        return true;
    }


    private void initializeDoctorsData() {
        doctorsList = new ArrayList<>();

        // HARDCODĂM AICI LISTA DE DOCTORI
        doctorsList.add(new Doctor("Dr. Emily Carter", "Cardiologist", "10 years", 4.9, "https://randomuser.me/api/portraits/women/44.jpg"));
        doctorsList.add(new Doctor("Dr. James Wilson", "Dermatologist", "8 years", 4.8, "https://randomuser.me/api/portraits/men/32.jpg"));
        doctorsList.add(new Doctor("Dr. Sofia Martinez", "Pediatrician", "12 years", 4.85, "https://randomuser.me/api/portraits/women/65.jpg"));
        doctorsList.add(new Doctor("Dr. David Kim", "Neurologist", "15 years", 4.7, "https://randomuser.me/api/portraits/men/55.jpg"));
        doctorsList.add(new Doctor("Dr. Laura Chen", "General Practitioner", "7 years", 4.6, "https://randomuser.me/api/portraits/women/50.jpg"));
        doctorsList.add(new Doctor("Dr. Marcus Brown", "Orthopedic Surgeon", "18 years", 4.95, "https://randomuser.me/api/portraits/men/40.jpg"));
        doctorsList.add(new Doctor("Dr. Angela White", "Ophthalmologist", "9 years", 4.88, "https://randomuser.me/api/portraits/women/30.jpg"));
        doctorsList.add(new Doctor("Dr. Robert Green", "Endocrinologist", "11 years", 4.65, "https://randomuser.me/api/portraits/men/20.jpg"));
        doctorsList.add(new Doctor("Dr. Hannah Singh", "Gynecologist", "13 years", 4.9, "https://randomuser.me/api/portraits/women/12.jpg"));
        doctorsList.add(new Doctor("Dr. Ethan Baker", "Oncologist", "14 years", 4.75, "https://randomuser.me/api/portraits/men/65.jpg"));
        doctorsList.add(new Doctor("Dr. Grace Lee", "Psychiatrist", "10 years", 4.8, "https://randomuser.me/api/portraits/women/33.jpg"));
        doctorsList.add(new Doctor("Dr. Michael Scott", "ENT Specialist", "9 years", 4.7, "https://randomuser.me/api/portraits/men/90.jpg"));
        doctorsList.add(new Doctor("Dr. Isabella Rossi", "Rheumatologist", "16 years", 4.85, "https://randomuser.me/api/portraits/women/70.jpg"));
        doctorsList.add(new Doctor("Dr. Daniel O’Connor", "Pulmonologist", "12 years", 4.68, "https://randomuser.me/api/portraits/men/73.jpg"));
        doctorsList.add(new Doctor("Dr. Aisha Ahmed", "Nephrologist", "10 years", 4.92, "https://randomuser.me/api/portraits/women/41.jpg"));

        // Inițializăm și setăm adaptorul cu lista de doctori
        doctorAdapter = new DoctorAdapter(doctorsList);
        recyclerView.setAdapter(doctorAdapter);

        // Mesaje Logcat pentru verificare
        if (doctorsList.isEmpty()) {
            Toast.makeText(this, "Lista de doctori este goală (eroare internă).", Toast.LENGTH_LONG).show();
            Log.d(TAG, "doctorsList is empty after initialization.");
        } else {
            Toast.makeText(this, "S-au încărcat " + doctorsList.size() + " doctori.", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Loaded " + doctorsList.size() + " doctors from hardcoded data.");
        }
    }
}