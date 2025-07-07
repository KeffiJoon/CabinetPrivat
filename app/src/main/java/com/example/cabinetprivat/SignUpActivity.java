package com.example.cabinetprivat;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    TextInputEditText usernameEditText, emailEditText, passwordEditText;
    Button signUpBtn;
    TextView signInLink;
    CheckBox termsCheckBox;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        usernameEditText = findViewById(R.id.usernameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        termsCheckBox = findViewById(R.id.termsCheckBox);
        signUpBtn = findViewById(R.id.signUpBtn);
        signInLink = findViewById(R.id.signInLink);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        signUpBtn.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (!termsCheckBox.isChecked()) {
                Toast.makeText(this, "Acceptă termenii și condițiile", Toast.LENGTH_SHORT).show();
                return;
            }

            if (email.isEmpty() || password.isEmpty() || username.isEmpty()) {
                Toast.makeText(this, "Completează toate câmpurile", Toast.LENGTH_SHORT).show();
                return;
            }

            String role = email.contains("cabinet.com") ? "doctor" : "pacient";

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener(authResult -> {
                        String uid = mAuth.getCurrentUser().getUid();

                        Map<String, Object> userMap = new HashMap<>();
                        userMap.put("email", email);
                        userMap.put("username", username);
                        userMap.put("role", role);

                        db.collection("users").document(uid)
                                .set(userMap)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(this, "Cont creat cu succes", Toast.LENGTH_SHORT).show();
                                    if (role.equals("doctor")) {
                                        startActivity(new Intent(SignUpActivity.this, DoctorProfileActivity.class));
                                    } else {
                                        startActivity(new Intent(SignUpActivity.this, PatientProfileActivity.class));
                                    }
                                    finish();
                                });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Eroare: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });

        signInLink.setOnClickListener(v -> {
            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
        });
    }
}










