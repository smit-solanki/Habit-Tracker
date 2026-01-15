package com.example.habit_traking;

import android.os.Bundle;
import android.graphics.Color;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis; // Added
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter; // Added
import java.util.ArrayList;
import java.util.List;

public class StatsActivity extends AppCompatActivity {

    private BarChart barChart;
    private TextView tvTotal, tvBest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        barChart = findViewById(R.id.barChart);
        tvTotal = findViewById(R.id.tvTotalHabits);
        tvBest = findViewById(R.id.tvBestStreak);

        loadStatsData();
    }

    private void loadStatsData() {
        HabitDatabase.getInstance(this).habitDao().getAllHabits().observe(this, habits -> {
            if (habits != null) {
                tvTotal.setText(String.valueOf(habits.size()));

                int maxStreak = 0;
                for (Habit h : habits) {
                    if (h.getStreakCount() > maxStreak) maxStreak = h.getStreakCount();
                }
                tvBest.setText(maxStreak + " Days");

                setupChart(habits);
            }
        });
    }

    private void setupChart(List<Habit> habits) {
        if (habits == null || habits.isEmpty()) {
            barChart.clear();
            return;
        }

        ArrayList<BarEntry> entries = new ArrayList<>();
        // Create an array to store names for the labels
        final String[] habitNames = new String[habits.size()];

        for (int i = 0; i < habits.size(); i++) {
            entries.add(new BarEntry(i, (float) habits.get(i).getStreakCount()));
            habitNames[i] = habits.get(i).getTitle(); // Store title
        }

        BarDataSet dataSet = new BarDataSet(entries, "Current Streaks");
        dataSet.setColor(Color.parseColor("#a3d5ff"));
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTextSize(12f);

        BarData data = new BarData(dataSet);
        barChart.setData(data);

        // --- X-AXIS LABEL LOGIC START ---
        XAxis xAxis = barChart.getXAxis();
        xAxis.setGranularity(1f); // Important: forces labels to 1 unit apart
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawGridLines(false);

        // This converts the number "0" to "Exercise", "1" to "Drink Water", etc.
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                if (index >= 0 && index < habitNames.length) {
                    return habitNames[index];
                }
                return "";
            }
        });

        // Enable touch and dragging
        barChart.setDragEnabled(true);
        barChart.setScaleEnabled(false); // Only allow scrolling, not zooming out too far

// Calculate how many bars to show at once
// If there are more than 5 habits, only show 5 at a time and let user scroll
        if (habits.size() > 5) {
            barChart.setVisibleXRangeMaximum(5f);
            barChart.moveViewToX(0f); // Start at the beginning
        }

        // If you have many habits, rotate labels so they don't crash into each other
        if (habits.size() > 4) {
            xAxis.setLabelRotationAngle(-45);
        }
        // --- X-AXIS LABEL LOGIC END ---

        // Styling
        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setTextColor(Color.WHITE);
        barChart.getAxisLeft().setTextColor(Color.WHITE);
        barChart.getAxisLeft().setAxisMinimum(0f);
        barChart.getAxisRight().setEnabled(false);

        barChart.animateY(1000);
        barChart.invalidate();
    }
}