package com.example.habit_traking;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler; // Added for time updates
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView; // Added
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.text.SimpleDateFormat; // Added
import java.util.Calendar;
import java.util.Locale; // Added
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ExtendedFloatingActionButton fabAdd;
    private ImageView btnStats;
    private TextView tvCurrentDate; // Added for the date display
    private final Handler timeHandler = new Handler();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Views
        recyclerView = findViewById(R.id.recyclerViewHabits);
        fabAdd = findViewById(R.id.fabAdd);
        btnStats = findViewById(R.id.btnStats);
        tvCurrentDate = findViewById(R.id.tvDate);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Start the logic to display date and handle midnight resets
        startDateAndResetCheck();

        // Click to add new habit
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddHabitActivity.class);
            startActivity(intent);
        });

        // Click to see stats
        btnStats.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, StatsActivity.class);
            startActivity(intent);
        });

        // Initialize Adapter
        HabitAdapter adapter = new HabitAdapter(this);
        recyclerView.setAdapter(adapter);

        // Observe Database Changes
        HabitDatabase.getInstance(this).habitDao().getAllHabits().observe(this, habits -> {
            // 1. Update the List/Adapter
            if (habits != null && !habits.isEmpty()) {
                adapter.setHabits(habits);
                findViewById(R.id.tvEmptyState).setVisibility(View.GONE);

                // --- START OF SUMMARY CARD CALCULATIONS ---
                int totalHabits = habits.size();
                int completedToday = 0;
                int maxStreak = 0;

                for (Habit habit : habits) {
                    // Count completed for the score
                    if (habit.isCompletedToday()) {
                        completedToday++;
                    }
                    // Find the highest streak
                    if (habit.getStreakCount() > maxStreak) {
                        maxStreak = habit.getStreakCount();
                    }
                }

                // Calculate Score Percentage
                int scorePercent = (completedToday * 100) / totalHabits;

                // Update the Summary Card TextViews
                TextView tvActiveCount = findViewById(R.id.activity);
                TextView tvScorePercent = findViewById(R.id.score);
                TextView tvMaxStreak = findViewById(R.id.streak);

                tvActiveCount.setText(String.valueOf(totalHabits));
                tvScorePercent.setText(scorePercent + "%");
                tvMaxStreak.setText(String.valueOf(maxStreak));
                // --- END OF SUMMARY CARD CALCULATIONS ---

            } else {
                adapter.setHabits(habits);
                findViewById(R.id.tvEmptyState).setVisibility(View.VISIBLE);

                // Reset card to zero if no habits exist
                ((TextView)findViewById(R.id.activity)).setText("0");
                ((TextView)findViewById(R.id.score)).setText("0%");
                ((TextView)findViewById(R.id.streak)).setText("0");
            }
        });
    }

    private void startDateAndResetCheck() {
        timeHandler.post(new Runnable() {
            @Override
            public void run() {
                // 1. UPDATE THE TEXTVIEW ON SCREEN
                Calendar now = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMM dd", Locale.getDefault());
                String formattedDate = sdf.format(now.getTime());
                tvCurrentDate.setText(formattedDate);

                // 2. CHECK FOR MIDNIGHT RESET
                SharedPreferences prefs = getSharedPreferences("HabitPrefs", MODE_PRIVATE);
                long lastResetTime = prefs.getLong("lastReset", 0);
                Calendar lastReset = Calendar.getInstance();
                lastReset.setTimeInMillis(lastResetTime);

                boolean isNewDay = now.get(Calendar.DAY_OF_YEAR) != lastReset.get(Calendar.DAY_OF_YEAR) ||
                        now.get(Calendar.YEAR) != lastReset.get(Calendar.YEAR);

                if (isNewDay) {
                    Executors.newSingleThreadExecutor().execute(() -> {
                        // First, punish streaks for failed habits
                        HabitDatabase.getInstance(MainActivity.this).habitDao().resetBrokenStreaks();
                        // Then, clear checkboxes for the new day
                        HabitDatabase.getInstance(MainActivity.this).habitDao().resetDailyCompletion();
                        // Save the reset time
                        prefs.edit().putLong("lastReset", System.currentTimeMillis()).apply();
                    });
                }

                // Repeat this check every 60 seconds (60000ms)
                timeHandler.postDelayed(this, 60000);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Stop the handler if the activity is destroyed to save memory
        timeHandler.removeCallbacksAndMessages(null);
    }
}