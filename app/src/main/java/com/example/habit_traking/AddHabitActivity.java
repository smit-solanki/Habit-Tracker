package com.example.habit_traking;

import android.os.Bundle;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
//import com.example.habit_traking.database.Habit;
//import com.example.habit_traking.database.HabitDatabase;
import java.util.concurrent.Executors;

public class AddHabitActivity extends AppCompatActivity {

    private TextInputEditText etName;
    private ChipGroup chipGroupCategory;
    private TimePicker timePicker;
    private MaterialButton btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_habit);

        etName = findViewById(R.id.etHabitName);
        chipGroupCategory = findViewById(R.id.chipGroup);
        //timePicker = findViewById(R.id.timePicker);
        btnSave = findViewById(R.id.btnSave);

        btnSave.setOnClickListener(v -> saveHabit());
    }

    private void saveHabit() {
        String name = etName.getText().toString().trim();


        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter a habit name", Toast.LENGTH_SHORT).show();
            return;
        }
        int checkedChipId = chipGroupCategory.getCheckedChipId();
        String category = "General";

        if (checkedChipId != View.NO_ID) {
            Chip selectedChip = findViewById(checkedChipId);
            category = selectedChip.getText().toString();
        }
        // Create new habit object
        Habit newHabit = new Habit(name, category);

        // Save to Room Database on background thread
        Executors.newSingleThreadExecutor().execute(() -> {
            HabitDatabase.getInstance(AddHabitActivity.this).habitDao().insert(newHabit);

            // Logic for scheduling notification goes here later

            runOnUiThread(() -> {
                Toast.makeText(AddHabitActivity.this, "Habit Saved!", Toast.LENGTH_SHORT).show();
                finish(); // Close activity and go back to Main
            });
        });
    }
}