
package com.example.cabinetprivat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Spinner;
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
import com.google.android.material.timepicker.MaterialTimePicker; // NOU
import com.google.android.material.timepicker.TimeFormat; // NOU

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;

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
    private Spinner doctorSelectionSpinner;

    // NOU: Elementele pentru selecția orei
    private Button selectTimeButton;
    private TextView selectedTimeTextView;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    // UI elements from Nav Header
    private TextView navHeaderName;
    private TextView navHeaderEmail;

    // Variabile pentru data și doctorul selectat
    private Long selectedDateInMillis = null; // Stochează data (fără oră)
    private String selectedDoctorName = null;

    // NOU: Variabile pentru ora selectată
    private int selectedHour = -1;
    private int selectedMinute = -1;
    private String formattedTime = null; // Ora formatată ca String, ex: "14:30"
    private View confirmAppointmentButton;


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
        confirmAppointmentButton = findViewById(R.id.button_confirm_appointment);

        // Inițializare CalendarView și setare listener
        calendarViewAppointment = findViewById(R.id.calendarView_appointment);
        calendarViewAppointment.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                // Setează doar data, resetând ora la 00:00:00 pentru moment
                Calendar selectedCalendar = Calendar.getInstance();
                selectedCalendar.set(year, month, dayOfMonth, 0, 0, 0); // Setează ora la 00:00:00
                selectedCalendar.set(Calendar.MILLISECOND, 0);
                selectedDateInMillis = selectedCalendar.getTimeInMillis(); // Doar data

                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                String formattedDate = format.format(selectedCalendar.getTime());
                selectedDateTextView.setText(getString(R.string.selected_date_placeholder, formattedDate));
                Toast.makeText(AppointmentsActivity.this, "Data selectată: " + formattedDate, Toast.LENGTH_SHORT).show();

                // NOU: Resetează selecția orei când data se schimbă
                selectedHour = -1;
                selectedMinute = -1;
                formattedTime = null;
                selectedTimeTextView.setText(getString(R.string.no_time_selected)); // Text pentru "Nicio oră selectată"
            }
        });

        // Setează data curentă ca selecție inițială în TextView și CalendarView
        Calendar initialCalendar = Calendar.getInstance();
        initialCalendar.set(Calendar.HOUR_OF_DAY, 0);
        initialCalendar.set(Calendar.MINUTE, 0);
        initialCalendar.set(Calendar.SECOND, 0);
        initialCalendar.set(Calendar.MILLISECOND, 0);
        selectedDateInMillis = initialCalendar.getTimeInMillis(); // Doar data

        SimpleDateFormat initialFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String initialFormattedDate = initialFormat.format(initialCalendar.getTime());
        selectedDateTextView.setText(getString(R.string.selected_date_placeholder, initialFormattedDate));
        calendarViewAppointment.setDate(initialCalendar.getTimeInMillis(), true, true);


        // NOU: Inițializare elemente TimePicker
        selectTimeButton = findViewById(R.id.button_select_time);
        selectedTimeTextView = findViewById(R.id.text_selected_time);

        selectTimeButton.setOnClickListener(v -> showTimePicker());


        // NOU: Inițializare Spinner pentru doctori
        doctorSelectionSpinner = findViewById(R.id.spinner_doctor_selection);
        setupDoctorSpinner();

        // Listener pentru butonul de confirmare programare
        confirmAppointmentButton.setOnClickListener(v -> saveAppointmentToFirestore());
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateNavHeader();
        setupDoctorSpinner();
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

    private void showTimePicker() {
        // Obține ora curentă ca valoare implicită pentru picker
        Calendar now = Calendar.getInstance();
        int currentHour = now.get(Calendar.HOUR_OF_DAY);
        int currentMinute = now.get(Calendar.MINUTE);

        // Dacă o oră a fost deja selectată, folosește-o pe aceea ca implicită
        if (selectedHour != -1 && selectedMinute != -1) {
            currentHour = selectedHour;
            currentMinute = selectedMinute;
        }

        MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H) // Sau CLOCK_12H
                .setHour(currentHour)
                .setMinute(currentMinute)
                .setTitleText("Selectează ora programării")
                .build();

        timePicker.addOnPositiveButtonClickListener(v -> {
            selectedHour = timePicker.getHour();
            selectedMinute = timePicker.getMinute();

            // Formatează ora pentru afișare și salvare
            formattedTime = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute);
            selectedTimeTextView.setText(getString(R.string.selected_time_placeholder, formattedTime));
            Toast.makeText(AppointmentsActivity.this, "Ora selectată: " + formattedTime, Toast.LENGTH_SHORT).show();
        });

        timePicker.show(getSupportFragmentManager(), "TIME_PICKER_TAG");
    }

    private void setupDoctorSpinner() {
        List<Doctor> allDoctors = HomeActivity.getAllDoctors();

        ArrayList<String> doctorNames = new ArrayList<>();
        doctorNames.add("Selectează un doctor");

        for (Doctor doctor : allDoctors) {
            doctorNames.add(doctor.getName() + " - " + doctor.getSpecialty());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, doctorNames);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        doctorSelectionSpinner.setAdapter(adapter);

        doctorSelectionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    selectedDoctorName = null;
                } else {
                    selectedDoctorName = allDoctors.get(position - 1).getName();
                    Toast.makeText(AppointmentsActivity.this, "Doctor selectat: " + selectedDoctorName, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedDoctorName = null;
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

        // NOU: Verifică dacă o oră a fost selectată
        if (selectedHour == -1 || selectedMinute == -1 || formattedTime == null) {
            Toast.makeText(this, "Selectează o oră pentru programare.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedDoctorName == null) {
            Toast.makeText(this, "Selectează un doctor pentru programare.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Combină data și ora într-un singur timestamp (dateInMillis complet)
        Calendar finalAppointmentCalendar = Calendar.getInstance();
        finalAppointmentCalendar.setTimeInMillis(selectedDateInMillis); // Setează data
        finalAppointmentCalendar.set(Calendar.HOUR_OF_DAY, selectedHour); // Setează ora
        finalAppointmentCalendar.set(Calendar.MINUTE, selectedMinute);   // Setează minutele
        finalAppointmentCalendar.set(Calendar.SECOND, 0);
        finalAppointmentCalendar.set(Calendar.MILLISECOND, 0);

        long finalDateInMillis = finalAppointmentCalendar.getTimeInMillis();

        // Validare suplimentară: Programarea să nu fie în trecut
        if (finalDateInMillis < System.currentTimeMillis()) {
            Toast.makeText(this, "Nu poți programa în trecut! Alege o dată și oră viitoare.", Toast.LENGTH_LONG).show();
            return;
        }

        Map<String, Object> appointment = new HashMap<>();
        appointment.put("userId", currentUser.getUid());
        appointment.put("userEmail", currentUser.getEmail());
        appointment.put("dateInMillis", finalDateInMillis); // Salvează timestamp-ul complet
        appointment.put("formattedDate", new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new java.util.Date(finalDateInMillis)));
        appointment.put("time", formattedTime); // Salvează ora ca string (ex: "14:30")
        appointment.put("doctorName", selectedDoctorName);
        appointment.put("status", "Pending"); // Setează un status inițial
        appointment.put("adminMessage", ""); // Mesaj inițial gol de la admin
        appointment.put("timestamp", FieldValue.serverTimestamp()); // Timestamp-ul creării programării pe server

        db.collection("appointments")
                .add(appointment)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(AppointmentsActivity.this, "Programare salvată cu succes!", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    // Resetează UI-ul după salvare
                    resetUI();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AppointmentsActivity.this, "Eroare la salvarea programării: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.w(TAG, "Error adding document", e);
                });
    }

    private void resetUI() {
        // Resetează calendarul la data curentă
        Calendar currentCal = Calendar.getInstance();
        currentCal.set(Calendar.HOUR_OF_DAY, 0);
        currentCal.set(Calendar.MINUTE, 0);
        currentCal.set(Calendar.SECOND, 0);
        currentCal.set(Calendar.MILLISECOND, 0);

        selectedDateInMillis = currentCal.getTimeInMillis();
        calendarViewAppointment.setDate(currentCal.getTimeInMillis(), true, true);

        SimpleDateFormat initialFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String initialFormattedDate = initialFormat.format(currentCal.getTime());
        selectedDateTextView.setText(getString(R.string.selected_date_placeholder, initialFormattedDate));


        // Resetează selecția orei
        selectedHour = -1;
        selectedMinute = -1;
        formattedTime = null;
        selectedTimeTextView.setText(getString(R.string.no_time_selected)); // Text pentru "Nicio oră selectată"

        // Resetează spinner-ul la prima opțiune
        doctorSelectionSpinner.setSelection(0);
        selectedDoctorName = null;
    }


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