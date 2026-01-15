package com.example.habit_traking;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "habit_table")
public class Habit {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String title;
    private String description;
    private int streakCount;
    private long lastCompletedDate; // Stores timestamp
    private boolean isCompletedToday;

    // Constructor
    public Habit(String title, String description) {
        this.title = title;
        this.description = description;
        this.streakCount = 0;
        this.lastCompletedDate = 0;
        this.isCompletedToday = false;
    }

    // Getters and Setters (Required for Room)
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getStreakCount() { return streakCount; }
    public void setStreakCount(int streakCount) { this.streakCount = streakCount; }
    public long getLastCompletedDate() { return lastCompletedDate; }
    public void setLastCompletedDate(long lastCompletedDate) { this.lastCompletedDate = lastCompletedDate; }
    public boolean isCompletedToday() { return isCompletedToday; }
    public void setCompletedToday(boolean completedToday) { isCompletedToday = completedToday; }
}
