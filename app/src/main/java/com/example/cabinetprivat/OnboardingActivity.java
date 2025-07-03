package com.example.cabinetprivat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class OnboardingActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        Button getStartedButton = findViewById(R.id.getStartedButton);
        Animation pulse = AnimationUtils.loadAnimation(this, R.anim.pulse_doctorii);
        getStartedButton.startAnimation(pulse);

        Button getStarted = findViewById(R.id.getStartedButton);
        TextView loginLink = findViewById(R.id.loginLink);

        getStarted.setOnClickListener(view -> {
            startActivity(new Intent(OnboardingActivity.this, SignUpActivity.class));
            finish();
        });

        loginLink.setOnClickListener(view -> {
            startActivity(new Intent(OnboardingActivity.this, LoginActivity.class));
            finish();
        });


    }
}

