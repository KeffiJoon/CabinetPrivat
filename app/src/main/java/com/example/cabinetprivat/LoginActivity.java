package com.example.cabinetprivat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log; // Adaugă acest import pentru Log
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser; // Adaugă acest import pentru FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore; // Adaugă acest import pentru Firestore
import com.google.firebase.firestore.DocumentReference; // Adaugă acest import
import com.google.firebase.firestore.DocumentSnapshot; // Adaugă acest import

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity"; // Definește TAG pentru log-uri

    TextInputEditText emailEditText, passwordEditText;
    Button signInBtn;
    TextView signUpLink;

    FirebaseAuth mAuth;
    FirebaseFirestore db; // Instanța Firestore

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.emailEditText); // Asigură-te că ID-urile sunt corecte în activity_login.xml
        passwordEditText = findViewById(R.id.passwordEditText);
        signInBtn = findViewById(R.id.signInBtn);
        signUpLink = findViewById(R.id.signUpLink);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance(); // Inițializează Firestore

        signInBtn.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Completează toate câmpurile", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> { // Folosim addOnCompleteListener pentru a prinde și erorile și succesul la un loc
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                checkUserRoleAndRedirect(user); // Aici apelăm metoda de verificare a rolului
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
            startActivity(new Intent(LoginActivity.this, SignUpActivity.class)); // Sau OnboardingActivity dacă asta e prima pagină de signup
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Verifică dacă utilizatorul este deja logat (persistent login)
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            checkUserRoleAndRedirect(currentUser); // Verifică rolul și redirecționează automat
        }
    }

    private void checkUserRoleAndRedirect(FirebaseUser user) {
        // Verifică dacă UID-ul utilizatorului există în colecția "admins"
        DocumentReference adminRef = db.collection("admins").document(user.getUid());
        adminRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            // Utilizatorul este admin
                            Toast.makeText(LoginActivity.this, "Autentificare ca admin reușită!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, AdminActivity.class);
                            startActivity(intent);
                        } else {
                            // Utilizatorul NU este admin, este un utilizator normal
                            Toast.makeText(LoginActivity.this, "Autentificare reușită!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, HelloActivity.class); // Redirecționează către HelloActivity (sau HomeActivity)
                            startActivity(intent);
                        }
                        finish(); // Închide LoginActivity pentru a preveni revenirea la ea cu butonul back
                    } else {
                        Log.e(TAG, "Eroare la verificare rol admin: ", task.getException());
                        Toast.makeText(LoginActivity.this, "Eroare la verificarea rolului. Reîncercați.", Toast.LENGTH_SHORT).show();
                        // În caz de eroare (ex: probleme de rețea, reguli Firestore incorecte),
                        // trimite-l la pagina implicită pentru utilizatori normali, pentru siguranță.
                        Intent intent = new Intent(LoginActivity.this, HelloActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
    }
}