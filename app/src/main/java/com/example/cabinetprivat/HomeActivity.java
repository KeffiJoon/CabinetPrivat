package com.example.cabinetprivat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cabinetprivat.adapters.DoctorAdapter;
import com.example.cabinetprivat.models.Doctor;
import com.google.android.material.navigation.NavigationView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "HomeActivity";

    private DrawerLayout drawerLayout;
    private RecyclerView recyclerView;
    private DoctorAdapter doctorAdapter;
    private List<Doctor> doctorsList; // Variabila locală pentru RecyclerView

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    // Nav Header UI
    private TextView navHeaderName;
    private TextView navHeaderEmail;

    // Lista statică de doctori, accesibilă din alte activități
    private static List<Doctor> sDoctorsList;

    // Lista STATICĂ HARDCODATĂ - PĂSTRATĂ AICI
    private final List<Doctor> doctorsListStatic = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Inițializare Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // 1. Inițializăm doctorsListStatic cu doctorii tăi hardcodati
        initializeHardcodedDoctors();

        // 2. Setează sDoctorsList inițial cu lista hardcodată
        // Acesta va fi fallback-ul dacă Firestore e gol sau inaccesibil
        sDoctorsList = new ArrayList<>(doctorsListStatic);

        // Configurare Toolbar
        Toolbar toolbar = findViewById(R.id.home_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        if (toolbarTitle != null) {
            toolbarTitle.setText("Doctors");
        }

        // Configurare Navigation Drawer
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Referințe la elementele din header-ul Navigation Drawer
        View headerView = navigationView.getHeaderView(0);
        navHeaderName = headerView.findViewById(R.id.textView_nav_header_name);
        navHeaderEmail = headerView.findViewById(R.id.textView_nav_header_email);
        updateNavHeader();

        // Inițializare RecyclerView
        recyclerView = findViewById(R.id.recyclerView_doctors);
        if (recyclerView == null) {
            Log.e(TAG, "RecyclerView with ID recyclerView_doctors not found in activity_home.xml.");
            Toast.makeText(this, "Eroare de inițializare: RecyclerView-ul nu a fost găsit.", Toast.LENGTH_LONG).show();
            return;
        }
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        // Încărcare doctori din Firestore (cu fallback la lista statică)
        loadDoctorsFromFirestore();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateNavHeader();
        // Poți apela loadDoctorsFromFirestore() aici din nou dacă vrei să reîncarci mereu
        // sau să folosești un listener Firestore în timp real (nu e cazul acum)
    }

    private void updateNavHeader() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            if (navHeaderName != null) navHeaderName.setText("Welcome!");
            if (navHeaderEmail != null) navHeaderEmail.setText("Not Logged In");
            return;
        }
        String displayName = currentUser.getDisplayName();
        String email = currentUser.getEmail();
        if (navHeaderName != null) {
            navHeaderName.setText(displayName != null && !displayName.isEmpty() ? displayName : "Hello!");
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
        } else if (id == R.id.nav_appointments) {
            Intent intent = new Intent(HomeActivity.this, AppointmentsActivity.class);
            startActivity(intent);
            finish();
        }else if (id==R.id.nav_info){
            Intent intent=new Intent (HomeActivity.this,AboutUsActivity.class);
        startActivity(intent);
        }else if (id == R.id.nav_profile) {
            Intent intent = new Intent(HomeActivity.this, HelloActivity.class);
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

    // Metodă pentru încărcarea doctorilor din Firestore
    private void loadDoctorsFromFirestore() {
        db.collection("doctors")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Doctor> fetchedDoctors = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Mapăm fiecare document din Firestore la un obiect Doctor
                            // Asigură-te că numele câmpurilor din Firestore sunt EXACT ca cele din Doctor.java
                            String name = document.getString("name");
                            String specialty = document.getString("specialty"); // "specialty", NU "speciality"
                            String yearsOfExperience = document.getString("yearsOfExperience"); // "yearsOfExperience", NU "experience"
                            Double rating = document.getDouble("rating");
                            String imageUrl = document.getString("imageUrl");

                            if (name != null && specialty != null && yearsOfExperience != null && rating != null) {
                                fetchedDoctors.add(new Doctor(name, specialty, yearsOfExperience, rating, imageUrl));
                            } else {
                                Log.w(TAG, "Document " + document.getId() + " is missing required fields or has invalid data types.");
                            }
                        }

                        if (!fetchedDoctors.isEmpty()) {
                            // Dacă s-au găsit doctori în Firestore, îi folosim pe aceia
                            sDoctorsList = new ArrayList<>(fetchedDoctors);
                            Toast.makeText(this, "S-au încărcat " + sDoctorsList.size() + " doctori din Firestore.", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Loaded " + sDoctorsList.size() + " doctors from Firestore.");
                        } else {
                            // Dacă Firestore e gol, folosim lista statică hardcodată
                            sDoctorsList = new ArrayList<>(doctorsListStatic);
                            Toast.makeText(this, "Firestore-ul este gol. Se folosesc doctorii hardcodati (" + sDoctorsList.size() + ").", Toast.LENGTH_LONG).show();
                            Log.d(TAG, "Firestore is empty. Using hardcoded doctors.");
                        }
                    } else {
                        // Dacă interogarea Firestore eșuează (ex: permisiuni, lipsă internet), folosim lista statică
                        sDoctorsList = new ArrayList<>(doctorsListStatic);
                        Toast.makeText(this, "Eroare la încărcarea doctorilor din Firestore. Se folosesc doctorii hardcodati (" + sDoctorsList.size() + ").", Toast.LENGTH_LONG).show();
                        Log.e(TAG, "Error getting documents from Firestore, falling back to hardcoded list: ", task.getException());
                    }

                    // Actualizăm RecyclerView-ul cu lista finală (Firestore sau statică)
                    doctorsList = sDoctorsList; // doctorsList locală este setată la sDoctorsList pentru adaptor
                    doctorAdapter = new DoctorAdapter(doctorsList);
                    recyclerView.setAdapter(doctorAdapter);
                });
    }

    // Metodă pentru a inițializa lista hardcodată de doctori
    private void initializeHardcodedDoctors() {
        doctorsListStatic.add(new Doctor("Dr. Grace Lee", "Psychiatrist", "10 years", 4.8, "https://randomuser.me/api/portraits/women/65.jpg"));
        doctorsListStatic.add(new Doctor("Dr. Michael Scott", "ENT Specialist", "9 years", 4.7, "https://randomuser.me/api/portraits/men/33.jpg"));
        doctorsListStatic.add(new Doctor("Dr. Isabella Rodriguez", "Rheumatologist", "16 years", 4.9, "https://randomuser.me/api/portraits/women/22.jpg"));
        doctorsListStatic.add(new Doctor("Dr. Daniel O'Connor", "Pulmonologist", "12 years", 4.7, "https://randomuser.me/api/portraits/men/50.jpg"));
        doctorsListStatic.add(new Doctor("Dr. Aisha Ahmed", "Nephrologist", "10 years", 4.9, "https://randomuser.me/api/portraits/women/44.jpg"));
        doctorsListStatic.add(new Doctor("Dr. Sofia Karlsson", "Neurologist", "14 years", 4.9, "https://randomuser.me/api/portraits/women/1.jpg"));
        doctorsListStatic.add(new Doctor("Dr. Ben Chen", "Pediatrician", "7 years", 4.6, "https://randomuser.me/api/portraits/men/2.jpg"));
        doctorsListStatic.add(new Doctor("Dr. Clara Schmidt", "Gastroenterologist", "11 years", 4.8, "https://randomuser.me/api/portraits/women/3.jpg"));
        doctorsListStatic.add(new Doctor("Dr. Omar Khan", "Urologist", "13 years", 4.7, "https://randomuser.me/api/portraits/men/4.jpg"));
        doctorsListStatic.add(new Doctor("Dr. Lena Petrova", "Endocrinologist", "9 years", 4.9, "https://randomuser.me/api/portraits/women/5.jpg"));
        doctorsListStatic.add(new Doctor("Dr. Alex Ivanov", "Oncologist", "18 years", 5.0, "https://randomuser.me/api/portraits/men/6.jpg"));
        doctorsListStatic.add(new Doctor("Dr. Maya Singh", "Ophthalmologist", "6 years", 4.5, "https://randomuser.me/api/portraits/women/7.jpg"));
        doctorsListStatic.add(new Doctor("Dr. Noah Green", "Anesthesiologist", "15 years", 4.8, "https://randomuser.me/api/portraits/men/8.jpg"));
        doctorsListStatic.add(new Doctor("Dr. Olivia White", "Immunologist", "10 years", 4.9, "https://randomuser.me/api/portraits/women/9.jpg"));
        doctorsListStatic.add(new Doctor("Dr. Liam Black", "Rheumatologist", "17 years", 4.9, "https://randomuser.me/api/portraits/men/10.jpg"));
    }

    // Metodă statică pentru a obține lista de doctori, accesibilă de oriunde
    public static List<Doctor> getAllDoctors() {
        return Collections.unmodifiableList(sDoctorsList != null ? sDoctorsList : new ArrayList<>());
    }
}