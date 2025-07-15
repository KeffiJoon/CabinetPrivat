package com.example.cabinetprivat; // Asigură-te că pachetul este corect

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar; // Import pentru Toolbar
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout; // Import pentru DrawerLayout

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem; // Import pentru MenuItem
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.navigation.NavigationView; // Import pentru NavigationView

import com.google.firebase.auth.FirebaseAuth; // Import pentru Firebase Auth
import com.google.firebase.auth.FirebaseUser; // Import pentru Firebase User
import java.util.Objects; // Import pentru Objects.requireNonNull

public class AboutUsActivity extends AppCompatActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {

    private GoogleMap mMap;
    // Coordonatele cabinetului în Galați, România. Ajustează-le cu locația reală!
    private LatLng clinicLocation = new LatLng(45.4333, 28.0500); // Exemplu Galați
    private String clinicName = "Cabinetul Privat ABC"; // Numele cabinetului tău
    private String clinicAddress = "Strada Exemplului Nr. 10, Orașul Tău, Județul Tău, Cod Poștal 123456"; // Adresa completă

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    // Elemente pentru Navigation Drawer
    private DrawerLayout drawerLayout;
    private FirebaseAuth mAuth; // Instanță Firebase Auth
    private TextView navHeaderName;
    private TextView navHeaderEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        mAuth = FirebaseAuth.getInstance(); // Inițializare Firebase Auth

        // --- Configurare Toolbar ---
        Toolbar toolbar = findViewById(R.id.about_us_toolbar); // ID-ul toolbar-ului din about_us.xml
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        TextView toolbarTitle = findViewById(R.id.toolbar_title); // ID-ul TextView-ului pentru titlu în toolbar
        if (toolbarTitle != null) {
            toolbarTitle.setText(R.string.about_us_title); // Setează titlul din strings.xml
        }

        // --- Configurare Navigation Drawer ---
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
        updateNavHeader(); // Actualizează informațiile din header

        // --- Inițializare Hărți (există deja) ---
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);

        // --- Inițializare Buton Direcții (există deja) ---
        Button getDirectionsButton = findViewById(R.id.button_get_directions);
        getDirectionsButton.setOnClickListener(v -> getDirections());

        // --- Inițializare Date Contact (există deja) ---
        TextView phoneNumberText = findViewById(R.id.text_phone_number);
        TextView emailAddressText = findViewById(R.id.text_email_address);

        // Click Listener pentru numărul de telefon
        phoneNumberText.setOnClickListener(v -> {
            String phoneNumber = phoneNumberText.getText().toString().trim();
            if (!phoneNumber.isEmpty()) {
                Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                dialIntent.setData(Uri.parse("tel:" + phoneNumber));
                if (dialIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(dialIntent);
                } else {
                    Toast.makeText(this, "No application found to handle phone calls.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Click Listener pentru adresa de email
        emailAddressText.setOnClickListener(v -> {
            String emailAddress = emailAddressText.getText().toString().trim();
            if (!emailAddress.isEmpty()) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:")); // doar aplicațiile de email răspund la asta
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{emailAddress});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Întrebare de la aplicația mobilă");

                if (emailIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(emailIntent);
                } else {
                    Toast.makeText(this, "No email application found.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateNavHeader(); // Asigură-te că antetul este actualizat la revenirea pe pagină
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


    /**
     * Gestionarea click-urilor pe itemii din Navigation Drawer
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Închide drawer-ul înainte de a naviga
        drawerLayout.closeDrawer(GravityCompat.START);

        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Dacă ești deja pe Home, nu face nimic sau arată un toast.
            // Dacă ești pe o altă pagină și vrei să te duci la Home:
            Intent intent = new Intent(AboutUsActivity.this, HomeActivity.class);
            startActivity(intent);
            finish(); // Închide AboutUsActivity pentru a nu avea un stack prea mare
        } else if (id == R.id.nav_appointments) {
            Intent intent = new Intent(AboutUsActivity.this, AppointmentsActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_info) {
            // Ești deja pe pagina About Us, nu face nimic
            Toast.makeText(this, "Ești deja pe pagina Despre Noi", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_profile) {
            Intent intent = new Intent(AboutUsActivity.this, HelloActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_logout) {
            mAuth.signOut();
            Toast.makeText(this, "Te-ai deconectat cu succes.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, OnboardingActivity.class); // Sau LoginActivity
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
        return true;
    }

    /**
     * Callback pentru harta Google Maps
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Adaugă un marker la locația cabinetului
        mMap.addMarker(new MarkerOptions().position(clinicLocation).title(clinicName).snippet(clinicAddress));
        // Mută camera la locația cabinetului cu un anumit nivel de zoom
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(clinicLocation, 15));

        // enableMyLocation(); // Dezactivează sau comentează asta dacă nu vrei să ceri permisiuni de locație aici
    }

    // Metode pentru permisiuni de locație și direcții (rămân la fel)
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (mMap != null) {
                mMap.setMyLocationEnabled(true);
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation();
            } else {
                Toast.makeText(this, "Permisiunea de locație refuzată. Nu se poate afișa locația curentă.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getDirections() {
        String uri = "http://maps.google.com/maps?daddr=" + clinicLocation.latitude + "," + clinicLocation.longitude + "&q=" + Uri.encode(clinicName);

        Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        mapIntent.setPackage("com.google.android.apps.maps");

        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            Toast.makeText(this, "Aplicația Google Maps nu a fost găsită. Instalați-o pentru a obține direcții.", Toast.LENGTH_LONG).show();
            Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            startActivity(webIntent);
        }
    }
}
