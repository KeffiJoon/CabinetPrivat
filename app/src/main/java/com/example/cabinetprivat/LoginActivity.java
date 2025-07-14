package com.example.cabinetprivat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils; // Importă TextUtils
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    TextInputEditText emailEditText, passwordEditText;
    Button signInBtn;
    TextView signUpLink;
    TextView forgotPasswordLink; // Adaugă referința pentru link-ul de uitare parolă

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        signInBtn = findViewById(R.id.signInBtn);
        signUpLink = findViewById(R.id.signUpLink);
        forgotPasswordLink = findViewById(R.id.forgotPasswordLink); // Găsește link-ul după ID

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        signInBtn.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Completează toate câmpurile", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                checkUserRoleAndRedirect(user);
                            } else {
                                Toast.makeText(LoginActivity.this, "Utilizator nul după login.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Autentificare eșuată: " + task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
        });

        signUpLink.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
        });

        // Setează OnClickListener pentru link-ul de uitare parolă
        forgotPasswordLink.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim(); // Preluăm emailul dacă e introdus
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            if (!TextUtils.isEmpty(email)) {
                intent.putExtra("email_for_reset", email); // Trimite emailul către ForgotPasswordActivity
            }
            startActivity(intent);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            checkUserRoleAndRedirect(currentUser);
        }
    }

    private void checkUserRoleAndRedirect(FirebaseUser user) {
        DocumentReference adminRef = db.collection("admins").document(user.getUid());
        adminRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            Toast.makeText(LoginActivity.this, "Autentificare ca admin reușită!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, AdminActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(LoginActivity.this, "Autentificare reușită!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, HelloActivity.class);
                            startActivity(intent);
                        }
                        finish();
                    } else {
                        Log.e(TAG, "Eroare la verificare rol admin: ", task.getException());
                        Toast.makeText(LoginActivity.this, "Eroare la verificarea rolului. Reîncercați.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, HelloActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
    }
}