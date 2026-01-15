package com.example.habit_traking;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Hide the ActionBar for a clean splash
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Simple Fade-in Animation for the logo
        ImageView logo = findViewById(R.id.logo);
        AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(1500);
        logo.startAnimation(fadeIn);

        // Delay for 3 seconds then go to MainActivity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // Close splash so user can't go back to it
            }
        }, 3000);
    }
}