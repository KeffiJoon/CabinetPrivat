package com.example.cabinetprivat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.airbnb.lottie.LottieAnimationView; // Import necesar pentru Lottie

public class ForgotPasswordActivity extends AppCompatActivity {

    private TextInputEditText emailEditText;
    private Button resetPasswordButton;
    private TextView backToLoginLink;
    private LottieAnimationView lottieAnimationView; // Declarație pentru animația Lottie

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        emailEditText = findViewById(R.id.emailEditText);
        resetPasswordButton = findViewById(R.id.resetPasswordButton);
        backToLoginLink = findViewById(R.id.backToLoginLink);
        lottieAnimationView = findViewById(R.id.lottie_forgot_password); // Inițializare LottieAnimationView

        mAuth = FirebaseAuth.getInstance();

        // Preluare email transmis de LoginActivity (opțional)
        if (getIntent().hasExtra("email_for_reset")) {
            String prefilledEmail = getIntent().getStringExtra("email_for_reset");
            emailEditText.setText(prefilledEmail);
        }

        // Deoarece am setat lottie_autoPlay="true" și lottie_loop="true" în XML,
        // animația va porni și va rula automat. Nu este nevoie de cod suplimentar aici pentru a o afișa.
        // Dacă ai dori să o controlezi manual:
        // lottieAnimationView.setAnimation("forgotp.json"); // Se setează fișierul JSON
        // lottieAnimationView.playAnimation(); // Se pornește animația
        // lottieAnimationView.loop(true); // Se setează să se repete

        resetPasswordButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(ForgotPasswordActivity.this, "Please enter your email address.", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(ForgotPasswordActivity.this, "Password reset email sent. Please check your inbox (and Spam folder).", Toast.LENGTH_LONG).show();
                            finish(); // Close ForgotPasswordActivity
                        } else {
                            String errorMessage = "Error sending reset email: ";
                            if (task.getException() != null) {
                                errorMessage += task.getException().getMessage();
                            }
                            Toast.makeText(ForgotPasswordActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        }
                    });
        });

        backToLoginLink.setOnClickListener(v -> {
            finish(); // Close this activity
        });
    }
}
