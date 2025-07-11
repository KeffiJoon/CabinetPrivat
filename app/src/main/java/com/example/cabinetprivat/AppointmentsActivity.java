package com.example.cabinetprivat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView; // NOU
import android.widget.ArrayAdapter; // NOU
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Spinner; // NOU
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.cabinetprivat.models.Doctor;
import com.google.android.material.navigation.NavigationView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue; // Pentru timestamp

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

public class AppointmentsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "AppointmentsActivity";

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private TextView selectedDateTextView;
    private CalendarView calendarViewAppointment;
    private Spinner doctorSelectionSpinner; // NOU: Spinner pentru doctori

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    // UI elements from Nav Header
    private TextView navHeaderName;
    private TextView navHeaderEmail;

    // Variabile pentru data și doctorul selectat
    private Long selectedDateInMillis = null;
    private String selectedDoctorName = null; // NOU: Numele doctorului selectat

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointments);

        // Inițializare Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Inițializare UI (Toolbar și Navigation Drawer)
        toolbar = findViewById(R.id.appointments_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        if (toolbarTitle != null) {
            toolbarTitle.setText("Appointments");
        }

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Referințe la elementele din header-ul Navigation Drawer
        View headerView = navigationView.getHeaderView(0);
        navHeaderName = headerView.findViewById(R.id.textView_nav_header_name);
        navHeaderEmail = headerView.findViewById(R.id.textView_nav_header_email);
        updateNavHeader();

        // Inițializare elemente specifice AppointmentsActivity
        selectedDateTextView = findViewById(R.id.text_selected_date);
        Button confirmAppointmentButton = findViewById(R.id.button_confirm_appointment);

        // Inițializare CalendarView și setare listener
        calendarViewAppointment = findViewById(R.id.calendarView_appointment);
        calendarViewAppointment.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                Calendar selectedCalendar = Calendar.getInstance();
                selectedCalendar.set(year, month, dayOfMonth);
                selectedDateInMillis = selectedCalendar.getTimeInMillis();
                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                String formattedDate = format.format(selectedCalendar.getTime());
                selectedDateTextView.setText(getString(R.string.selected_date_placeholder, formattedDate));
                Toast.makeText(AppointmentsActivity.this, "Data selectată: " + formattedDate, Toast.LENGTH_SHORT).show();
            }
        });

        // Setarea datei curente ca selecție inițială în TextView
        Calendar initialCalendar = Calendar.getInstance();
        initialCalendar.setTimeInMillis(calendarViewAppointment.getDate());
        SimpleDateFormat initialFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String initialFormattedDate = initialFormat.format(initialCalendar.getTime());
        selectedDateTextView.setText(getString(R.string.selected_date_placeholder, initialFormattedDate));
        selectedDateInMillis = initialCalendar.getTimeInMillis();

        // NOU: Inițializare Spinner pentru doctori
        doctorSelectionSpinner = findViewById(R.id.spinner_doctor_selection);
        setupDoctorSpinner(); // Metodă pentru configurarea spinner-ului

        // Listener pentru butonul de confirmare programare
        confirmAppointmentButton.setOnClickListener(v -> saveAppointmentToFirestore());
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateNavHeader();
        setupDoctorSpinner(); // Reîncarcă doctorii în Spinner la revenirea în activitate, în caz că lista s-a actualizat în HomeActivity
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
            navHeaderName.setText(displayName != null && !displayName.isEmpty() ? displayName : "Guest User");
        }
        if (navHeaderEmail != null) {
            navHeaderEmail.setText(email != null && !email.isEmpty() ? email : "No Email");
        }
    }

    // NOU: Metodă pentru configurarea Spinner-ului pentru doctori
    private void setupDoctorSpinner() {
        // Preia lista de doctori din HomeActivity
        List<Doctor> allDoctors = HomeActivity.getAllDoctors();

        ArrayList<String> doctorNames = new ArrayList<>();
        doctorNames.add("Selectează un doctor"); // Opțiune implicită (position 0)

        for (Doctor doctor : allDoctors) {
            doctorNames.add(doctor.getName() + " - " + doctor.getSpecialty());
        }

        // Creează un ArrayAdapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, doctorNames);

        // Specifică layout-ul de utilizat când lista de opțiuni apare
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Aplică adapter-ul la spinner
        doctorSelectionSpinner.setAdapter(adapter);

        // Setează un listener pentru a detecta selecția utilizatorului
        doctorSelectionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                if (position == 0) { // Dacă este selectată opțiunea implicită
                    selectedDoctorName = null; // Marchează că nu a fost selectat un doctor valid
                } else {
                    // Extrage doar numele doctorului dacă vrei să salvezi doar numele curat
                    // Sau salvează string-ul complet "Nume - Specialitate"
                    selectedDoctorName = allDoctors.get(position - 1).getName(); // -1 pentru că poziția 0 e "Selectează..."
                    Toast.makeText(AppointmentsActivity.this, "Doctor selectat: " + selectedDoctorName, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedDoctorName = null; // Niciun doctor selectat
            }
        });
    }

    private void saveAppointmentToFirestore() {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, "Trebuie să fii logat pentru a face o programare.", Toast.LENGTH_LONG).show();
            return;
        }

        if (selectedDateInMillis == null) {
            Toast.makeText(this, "Selectează o dată pentru programare.", Toast.LENGTH_SHORT).show();
            return;
        }

        // NOU: Verifică dacă un doctor a fost selectat
        if (selectedDoctorName == null || selectedDoctorName.equals("Selectează un doctor")) { // Added check for default spinner text
            Toast.makeText(this, "Selectează un doctor pentru programare.", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> appointment = new HashMap<>();
        appointment.put("userId", currentUser.getUid());
        appointment.put("userEmail", currentUser.getEmail());
        appointment.put("dateInMillis", selectedDateInMillis);
        appointment.put("formattedDate", new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new java.util.Date(selectedDateInMillis)));
        appointment.put("doctorName", selectedDoctorName); // NOU: Salvează numele doctorului selectat
        appointment.put("timestamp", FieldValue.serverTimestamp()); // NOU: Adaugă un timestamp al creării programării

        db.collection("appointments")
                .add(appointment)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(AppointmentsActivity.this, "Programare salvată cu succes!", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    // Resetează UI-ul după salvare
                    selectedDateTextView.setText(getString(R.string.no_date_selected));
                    selectedDateInMillis = null;
                    calendarViewAppointment.setDate(System.currentTimeMillis(), true, true); // Resetează calendarul la data curentă
                    doctorSelectionSpinner.setSelection(0); // Resetează spinner-ul la prima opțiune
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AppointmentsActivity.this, "Eroare la salvarea programării: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.w(TAG, "Error adding document", e);
                });
    }

    // Gestionarea elementelor din Navigation Drawer (rămâne la fel)
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);

        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_appointments) {
            Toast.makeText(this, "Ești deja la Programări", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_profile) {
            Intent intent = new Intent(this, HelloActivity.class);
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
}
