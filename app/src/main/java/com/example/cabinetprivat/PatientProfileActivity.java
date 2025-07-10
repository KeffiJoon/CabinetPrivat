package com.example.cabinetprivat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class PatientProfileActivity extends AppCompatActivity {

    private EditText nameEditText, phoneEditText, dobEditText, allergiesEditText;
    private AutoCompleteTextView bloodTypeAutoComplete;
    private Button saveProfileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_profile);

        // Legare View-uri
        nameEditText = findViewById(R.id.editText_name);
        phoneEditText = findViewById(R.id.editText_phone);
        dobEditText = findViewById(R.id.editText_dob);
        bloodTypeAutoComplete = findViewById(R.id.autoComplete_blood_type);
        allergiesEditText = findViewById(R.id.editText_allergies);
        saveProfileButton = findViewById(R.id.button_save_profile);

        // Setup pentru AutoCompleteTextView cu grupe sanguine
        String[] bloodTypes = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, bloodTypes);
        bloodTypeAutoComplete.setAdapter(adapter);

        // La click pe Save -> Toast + Trecere la HelloActivity
        saveProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserProfile();
            }
        });
    }

    private void saveUserProfile() {
        // Poți adăuga validări aici
        String name = nameEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String dob = dobEditText.getText().toString().trim();
        String bloodType = bloodTypeAutoComplete.getText().toString().trim();
        String allergies = allergiesEditText.getText().toString().trim();

        if (name.isEmpty() || phone.isEmpty() || dob.isEmpty() || bloodType.isEmpty()) {
            Toast.makeText(this, "Completează toate câmpurile obligatorii.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Aici poți salva în Firebase sau local, momentan doar Toast
        Toast.makeText(this, "Profil salvat cu succes!", Toast.LENGTH_SHORT).show();

        // Trecere la HelloActivity
        Intent intent = new Intent(PatientProfileActivity.this, HelloActivity.class);
        startActivity(intent);
        finish();
    }
}



















