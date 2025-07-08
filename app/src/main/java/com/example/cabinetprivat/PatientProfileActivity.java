package com.example.cabinetprivat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cabinetprivat.R; // Asigură-te că R este importat corect
import com.example.cabinetprivat.models.Patient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText; // Importă TextInputEditText
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors; // Necesită API 24+

public class PatientProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    // Declare your UI elements as class members
    private TextInputEditText editTextName;
    private TextInputEditText editTextPhone;
    private TextInputEditText editTextDob;
    private AutoCompleteTextView autoCompleteBloodType;
    private TextInputEditText editTextAllergies;
    private Button buttonSaveProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_profile); // Set content view to your XML layout

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize UI elements using findViewById
        editTextName = findViewById(R.id.editText_name);
        editTextPhone = findViewById(R.id.editText_phone);
        editTextDob = findViewById(R.id.editText_dob);
        autoCompleteBloodType = findViewById(R.id.autoComplete_blood_type);
        editTextAllergies = findViewById(R.id.editText_allergies);
        buttonSaveProfile = findViewById(R.id.button_save_profile);

        // Setup Blood Type Dropdown
        String[] bloodTypes = getResources().getStringArray(R.array.blood_types_array); // Definește acest array în strings.xml
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, bloodTypes);
        autoCompleteBloodType.setAdapter(adapter);

        buttonSaveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserProfile();
            }
        });
    }

    private void saveUserProfile() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Utilizator nelogat.", Toast.LENGTH_SHORT).show();
            // Poate redirecționezi la ecranul de login
            return;
        }

        String name = editTextName.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        String dob = editTextDob.getText().toString().trim();
        String bloodType = autoCompleteBloodType.getText().toString().trim();
        String allergiesStr = editTextAllergies.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(dob)) {
            Toast.makeText(this, "Numele, telefonul și data nașterii sunt obligatorii.", Toast.LENGTH_LONG).show();
            return;
        }

        List<String> allergiesList = null;
        if (!TextUtils.isEmpty(allergiesStr)) {
            // Simplu split prin virgulă, poți adăuga validare mai bună
            // Asigură-te că API Level-ul țintă este 24 sau mai mare pentru Collectors.toList()
            allergiesList = Arrays.stream(allergiesStr.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
        }

        // Email-ul și UID-ul sunt preluate de la utilizatorul autentificat
        String email = currentUser.getEmail();
        String uid = currentUser.getUid();

        Patient patientData = new Patient(
                uid, // Folosim UID-ul ca ID al documentului pacientului
                name,
                email, // Email-ul este deja cunoscut de la autentificare
                phone,
                null, // profileImageUrl - poate fi adăugat ulterior
                dob,
                bloodType,
                allergiesList
        );

        // Salvează în Firestore
        // Folosim UID-ul utilizatorului ca ID al documentului în colecția "patients"
        // SetOptions.merge() va crea documentul dacă nu există sau va actualiza câmpurile dacă există
        db.collection("patients").document(uid)
                .set(patientData, SetOptions.merge()) // SetOptions.merge() este util dacă vrei să actualizezi parțial
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(PatientProfileActivity.this, "Profil salvat cu succes!", Toast.LENGTH_SHORT).show();
                        // Redirecționează către ecranul principal sau profilul pacientului
                        Intent intent = new Intent(PatientProfileActivity.this, HomePatientActivity.class);
                        // Poți trimite ID-ul pacientului dacă PatientProfileActivity se așteaptă la el
                        // intent.putExtra("PATIENT_ID", uid);
                        startActivity(intent);
                        finish(); // Închide acest activity
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(PatientProfileActivity.this, "Eroare la salvarea profilului: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Verifică dacă utilizatorul este logat
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // Nu ar trebui să ajungă aici dacă fluxul de autentificare e corect,
            // dar e bine să ai o verificare. Redirecționează la login.
            // startActivity(new Intent(this, LoginActivity.class));
            // finish();
        }
    }
}














