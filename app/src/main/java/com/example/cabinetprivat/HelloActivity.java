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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.cabinetprivat.adapters.AppointmentAdapter;
import com.example.cabinetprivat.models.Appointment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class HelloActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "HelloActivity"; // Use a specific TAG for this activity

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private TextView toolbarTitleTextView;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private TextView navHeaderName, navHeaderEmail;
    private TextView textDisplayName, textEmail, textUid, textCreationDate;

    private RecyclerView recyclerViewAppointments;
    private AppointmentAdapter appointmentAdapter;
    private List<Appointment> appointmentList;
    private LottieAnimationView lottieNoAppointments;
    private TextView textNoAppointments;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        toolbar = findViewById(R.id.hello_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        toolbarTitleTextView = findViewById(R.id.toolbar_title);
        if (toolbarTitleTextView != null) {
            toolbarTitleTextView.setText(R.string.profile_appointments_title);
        }

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        View headerView = navigationView.getHeaderView(0);
        navHeaderName = headerView.findViewById(R.id.textView_nav_header_name);
        navHeaderEmail = headerView.findViewById(R.id.textView_nav_header_email);

        textDisplayName = findViewById(R.id.text_display_name);
        textEmail = findViewById(R.id.text_email);
        textUid = findViewById(R.id.text_uid);
        textCreationDate = findViewById(R.id.text_creation_date);

        recyclerViewAppointments = findViewById(R.id.recyclerView_appointments);
        if (recyclerViewAppointments == null) {
            Log.e(TAG, "RecyclerView with ID recyclerView_appointments not found in activity_hello.xml.");
            Toast.makeText(this, "Initialization error: Appointments RecyclerView not found.", Toast.LENGTH_LONG).show();
        } else {
            recyclerViewAppointments.setLayoutManager(new LinearLayoutManager(this));
            appointmentList = new ArrayList<>();
            appointmentAdapter = new AppointmentAdapter(appointmentList);
            recyclerViewAppointments.setAdapter(appointmentAdapter);
        }

        lottieNoAppointments = findViewById(R.id.lottie_no_appointments);
        textNoAppointments = findViewById(R.id.text_no_appointments);

        updateUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String displayName = currentUser.getDisplayName();
            String email = currentUser.getEmail();
            String uid = currentUser.getUid();
            long creationTimestamp = currentUser.getMetadata() != null ? currentUser.getMetadata().getCreationTimestamp() : 0;

            Log.d(TAG, "Logged in User UID: " + uid); // Log the current user's UID

            if (navHeaderName != null) navHeaderName.setText(displayName != null && !displayName.isEmpty() ? displayName : "Guest User");
            if (navHeaderEmail != null) navHeaderEmail.setText(email != null && !email.isEmpty() ? email : "No Email");

            if (textDisplayName != null) textDisplayName.setText("Name: " + (displayName != null ? displayName : "N/A"));
            if (textEmail != null) textEmail.setText("Email: " + (email != null ? email : "N/A"));
            if (textUid != null) textUid.setText("User ID: " + uid);

            if (textCreationDate != null && creationTimestamp > 0) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                String formattedDate = sdf.format(new Date(creationTimestamp));
                textCreationDate.setText("Account created: " + formattedDate);
            } else if (textCreationDate != null) {
                textCreationDate.setText("Account created: N/A");
            }

            loadAppointmentsFromFirestore(currentUser.getUid());

        } else {
            Log.d(TAG, "User not logged in. Displaying generic info.");
            if (navHeaderName != null) navHeaderName.setText("Welcome!");
            if (navHeaderEmail != null) navHeaderEmail.setText("Not Logged In");

            if (textDisplayName != null) textDisplayName.setText("Name: Not Authenticated");
            if (textEmail != null) textEmail.setText("Email: Not Authenticated");
            if (textUid != null) textUid.setText("User ID: N/A");
            if (textCreationDate != null) textCreationDate.setText("Account created: N/A");

            Toast.makeText(this, "You are not logged in.", Toast.LENGTH_SHORT).show();
            displayNoAppointments(true);
            if (appointmentList != null) {
                appointmentList.clear();
                if (appointmentAdapter != null) {
                    appointmentAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    private void loadAppointmentsFromFirestore(String userId) {
        if (recyclerViewAppointments == null || appointmentList == null || appointmentAdapter == null) {
            Log.e(TAG, "Appointment UI components not initialized. Cannot load appointments.");
            return;
        }

        Log.d(TAG, "Attempting to load appointments for userId: " + userId); // Log the userId used in query

        db.collection("appointments")
                .whereEqualTo("userId", userId)
                .orderBy("dateInMillis", Query.Direction.ASCENDING)
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Firestore query successful. Documents found: " + task.getResult().size());
                        List<Appointment> fetchedAppointments = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            try {
                                Appointment appointment = document.toObject(Appointment.class);
                                appointment.setAppointmentId(document.getId());
                                fetchedAppointments.add(appointment);
                                Log.d(TAG, "Fetched Appointment: " + document.getId() + " - Date: " + appointment.getFormattedDate() + " - Status: " + appointment.getStatus());
                            } catch (Exception e) {
                                Log.e(TAG, "Error converting document to Appointment: " + document.getId(), e);
                                Log.e(TAG, "Problematic document data: " + document.getData());
                            }
                        }

                        // --- Filtering Logic Check ---
                        List<Appointment> futureAppointments = new ArrayList<>();
                        Calendar now = Calendar.getInstance();
                        now.set(Calendar.HOUR_OF_DAY, 0); // Reset time to start of day for comparison
                        now.set(Calendar.MINUTE, 0);
                        now.set(Calendar.SECOND, 0);
                        now.set(Calendar.MILLISECOND, 0);
                        long currentDayStartMillis = now.getTimeInMillis();
                        Log.d(TAG, "Current Day Start Millis: " + currentDayStartMillis + " (" + new Date(currentDayStartMillis) + ")");


                        for (Appointment app : fetchedAppointments) {
                            if (app.getDateInMillis() != null) {
                                // Important: We should compare against the exact appointment time if available,
                                // or the start of the appointment day. Let's compare against start of the day.
                                Calendar appointmentCalendar = Calendar.getInstance();
                                appointmentCalendar.setTimeInMillis(app.getDateInMillis());
                                appointmentCalendar.set(Calendar.HOUR_OF_DAY, 0); // Reset time for comparison
                                appointmentCalendar.set(Calendar.MINUTE, 0);
                                appointmentCalendar.set(Calendar.SECOND, 0);
                                appointmentCalendar.set(Calendar.MILLISECOND, 0);
                                long appDayStartMillis = appointmentCalendar.getTimeInMillis();

                                Log.d(TAG, "Comparing Appointment: " + app.getFormattedDate() + " " + app.getTime() +
                                        " (Millis: " + app.getDateInMillis() + ") " +
                                        "App Day Start Millis: " + appDayStartMillis +
                                        " with Current Day Start Millis: " + currentDayStartMillis);

                                if (appDayStartMillis >= currentDayStartMillis) { // If appointment day is today or in the future
                                    // If appointment is today, check if its time is in the future
                                    if (appDayStartMillis == currentDayStartMillis) {
                                        // Compare exact time if it's today
                                        if (app.getDateInMillis() >= Calendar.getInstance().getTimeInMillis()) {
                                            futureAppointments.add(app);
                                        } else {
                                            Log.d(TAG, "Filtered out (past time today): " + app.getFormattedDate() + " " + app.getTime());
                                        }
                                    } else {
                                        futureAppointments.add(app); // It's a future day
                                    }

                                } else {
                                    Log.d(TAG, "Filtered out (past date): " + app.getFormattedDate() + " " + app.getTime());
                                }
                            } else {
                                Log.w(TAG, "Appointment with missing dateInMillis: " + app.getAppointmentId());
                            }
                        }
                        // --- End Filtering Logic Check ---

                        appointmentList.clear();
                        appointmentList.addAll(futureAppointments);
                        appointmentAdapter.notifyDataSetChanged();

                        if (futureAppointments.isEmpty()) {
                            displayNoAppointments(true);
                            Toast.makeText(HelloActivity.this, "You have no future appointments.", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "No future appointments found for user: " + userId + " after filtering.");
                        } else {
                            displayNoAppointments(false);
                            Toast.makeText(HelloActivity.this, "Loaded " + futureAppointments.size() + " future appointments.", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Loaded " + futureAppointments.size() + " future appointments for user: " + userId + " after filtering.");
                        }
                    } else {
                        // This block runs if the Firestore query itself fails (e.g., security rules, index)
                        Toast.makeText(HelloActivity.this, "Error loading appointments: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                        Log.e(TAG, "Error getting appointments: " + task.getException().getMessage(), task.getException());
                        displayNoAppointments(true);
                    }
                });
    }

    private void displayNoAppointments(boolean show) {
        if (lottieNoAppointments == null || textNoAppointments == null || recyclerViewAppointments == null) {
            Log.e(TAG, "UI components for no appointments message not initialized.");
            return;
        }

        if (show) {
            lottieNoAppointments.setVisibility(View.VISIBLE);
            lottieNoAppointments.playAnimation();
            textNoAppointments.setVisibility(View.VISIBLE);
            recyclerViewAppointments.setVisibility(View.GONE);
        } else {
            lottieNoAppointments.setVisibility(View.GONE);
            lottieNoAppointments.pauseAnimation();
            textNoAppointments.setVisibility(View.GONE);
            recyclerViewAppointments.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);

        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_appointments) {
            Intent intent = new Intent(this, AppointmentsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_profile) {
            Toast.makeText(this, "You are already on the Profile page.", Toast.LENGTH_SHORT).show();
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