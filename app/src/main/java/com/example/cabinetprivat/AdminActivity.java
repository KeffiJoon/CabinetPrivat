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

import com.example.cabinetprivat.adapters.AdminAppointmentAdapter;
import com.example.cabinetprivat.models.Appointment; // Asigură-te că acest import este corect
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AdminActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "AdminActivity";

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private TextView navHeaderName, navHeaderEmail;

    private RecyclerView adminAppointmentsRecyclerView;
    private AdminAppointmentAdapter adminAppointmentAdapter;
    private List<Appointment> adminAppointmentList;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ListenerRegistration appointmentsListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Inițializare UI (Toolbar și Navigation Drawer)
        toolbar = findViewById(R.id.admin_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        TextView toolbarTitle = findViewById(R.id.admin_toolbar_title);
        if (toolbarTitle != null) {
            toolbarTitle.setText("Panou de Administrare");
        }

        drawerLayout = findViewById(R.id.drawer_layout_admin);
        navigationView = findViewById(R.id.nav_view_admin);
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

        // Inițializare RecyclerView
        adminAppointmentsRecyclerView = findViewById(R.id.recycler_view_admin_appointments);
        adminAppointmentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adminAppointmentList = new ArrayList<>();
        adminAppointmentAdapter = new AdminAppointmentAdapter(this, adminAppointmentList, db);
        adminAppointmentsRecyclerView.setAdapter(adminAppointmentAdapter);

        fetchAppointmentsForAdmin();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateNavHeader();
        // Listener-ul se re-atașează în fetchAppointmentsForAdmin() dacă este nevoie,
        // dar de obicei addSnapshotListener() este suficient de persistent.
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Detasează listener-ul pentru a evita memory leaks
        if (appointmentsListener != null) {
            appointmentsListener.remove();
        }
    }

    private void updateNavHeader() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            if (navHeaderName != null) navHeaderName.setText("Bine ai venit!");
            if (navHeaderEmail != null) navHeaderEmail.setText("Nu ești logat");
            return;
        }
        String displayName = currentUser.getDisplayName();
        String email = currentUser.getEmail();
        if (navHeaderName != null) {
            navHeaderName.setText(displayName != null && !displayName.isEmpty() ? displayName : "Utilizator Admin");
        }
        if (navHeaderEmail != null) {
            navHeaderEmail.setText(email != null && !email.isEmpty() ? email : "Fără email");
        }
    }

    private void fetchAppointmentsForAdmin() {
        // Detasează listener-ul vechi înainte de a adăuga unul nou, pentru a preveni duplicarea
        if (appointmentsListener != null) {
            appointmentsListener.remove();
        }

        // Ascultă toate programările, sortate după timestamp (cele mai recente înregistrate primele)
        appointmentsListener = db.collection("appointments")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e(TAG, "Eroare la ascultare Firebase: ", error);
                        Toast.makeText(AdminActivity.this, "Eroare la încărcarea programărilor: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (value != null) {
                        adminAppointmentList.clear();
                        for (QueryDocumentSnapshot doc : value) {
                            try {
                                Appointment appointment = doc.toObject(Appointment.class);
                                // Setează appointmentId din ID-ul documentului Firestore
                                appointment.setAppointmentId(doc.getId());
                                adminAppointmentList.add(appointment);
                            } catch (Exception e) {
                                Log.e(TAG, "Eroare la conversia documentului in obiect Appointment (ID: " + doc.getId() + "): ", e);
                            }
                        }
                        adminAppointmentAdapter.notifyDataSetChanged();
                        if (adminAppointmentList.isEmpty()) {
                            Toast.makeText(AdminActivity.this, "Nu există programări de afișat.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.d(TAG, "Date curente: null");
                    }
                });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START); // Închide sertarul după selecție

        int id = item.getItemId();

        if (id == R.id.nav_admin_dashboard) {
            Toast.makeText(this, "Ești deja la Panoul de Administrare", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_logout) {
            mAuth.signOut();
            Toast.makeText(this, "Deconectare reușită.", Toast.LENGTH_SHORT).show();
            // Asigură o navigare curată către ecranul de onboarding/login
            Intent intent = new Intent(this, OnboardingActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish(); // Închide AdminActivity
        }
        return true; // Returnează true pentru a marca elementul ca selectat
    }

    /**
     * Această metodă este apelată de către adapter pentru a actualiza statusul unei programări în Firestore.
     * @param appointmentId ID-ul documentului programării.
     * @param newStatus Noul status (ex: "Approved", "Rejected").
     * @param adminMessage Mesajul opțional al administratorului.
     */
    public void updateAppointmentStatusInFirestore(String appointmentId, String newStatus, String adminMessage) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("status", newStatus);
        updates.put("adminMessage", adminMessage);

        db.collection("appointments").document(appointmentId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(AdminActivity.this, "Status actualizat la: " + newStatus, Toast.LENGTH_SHORT).show();
                    // Lista se va actualiza automat datorită SnapshotListener-ului
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AdminActivity.this, "Eroare la actualizarea statusului: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Eroare la actualizarea statusului programării cu ID: " + appointmentId, e);
                });
    }
}
