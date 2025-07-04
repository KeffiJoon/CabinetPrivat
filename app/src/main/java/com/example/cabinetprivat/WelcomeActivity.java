package com.example.cabinetprivat;


import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

public class WelcomeActivity extends AppCompatActivity {

    MaterialButton urgentCareButton;
    BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        urgentCareButton = findViewById(R.id.btnUrgentCare);
        bottomNav = findViewById(R.id.bottom_nav);

        urgentCareButton.setOnClickListener(view ->
                Toast.makeText(this, "Urgent care tapped!", Toast.LENGTH_SHORT).show()
        );

        bottomNav.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_home:
                    Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.nav_add:
                    Toast.makeText(this, "Add", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.nav_chat:
                    Toast.makeText(this, "Chat", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.nav_profile:
                    Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show();
                    return true;
            }
            return false;
        });
    }
}
