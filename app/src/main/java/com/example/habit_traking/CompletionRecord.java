package com.example.habit_traking;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "completion_table")
public class CompletionRecord {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public int habitId;
    public long timestamp; // The exact time/date it was checked

    public CompletionRecord(int habitId, long timestamp) {
        this.habitId = habitId;
        this.timestamp = timestamp;
    }
}