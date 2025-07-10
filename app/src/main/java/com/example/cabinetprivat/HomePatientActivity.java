package com.example.cabinetprivat;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cabinetprivat.models.PatientModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

public class HomePatientActivity extends AppCompatActivity {

    TextView nameTV, phoneTV, dobTV, bloodTypeTV, allergiesTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_patient);

        nameTV = findViewById(R.id.text_patient_name);
        phoneTV = findViewById(R.id.text_patient_phone);
        dobTV = findViewById(R.id.text_patient_dob);
        bloodTypeTV = findViewById(R.id.text_patient_blood_type);
        allergiesTV = findViewById(R.id.text_patient_allergies);

        loadUserData();
    }

    private void loadUserData() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Patients").child(uid);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                PatientModel patient = snapshot.getValue(PatientModel.class);
                if (patient != null) {
                    nameTV.setText(patient.getFullName());
                    phoneTV.setText(patient.getPhone());
                    dobTV.setText(patient.getDob());
                    bloodTypeTV.setText(patient.getBloodType());
                    allergiesTV.setText(patient.getAllergies());
                } else {
                    Toast.makeText(HomePatientActivity.this, "No data found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomePatientActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

