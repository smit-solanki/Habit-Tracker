package com.example.habit_traking;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface HabitDao {

    @Insert
    void insert(Habit habit);

    @Update
    void update(Habit habit);

    @Delete
    void deleteHabit(Habit habit);

    @Query("SELECT * FROM habit_table ORDER BY id DESC")
    LiveData<List<Habit>> getAllHabits();

    @Query("UPDATE habit_table SET isCompletedToday = 0")
    void resetDailyCompletion(); // Run this at midnight

    // If they didn't finish yesterday, reset their streak to 0
    @Query("UPDATE habit_table SET streakCount = 0 WHERE isCompletedToday = 0")
    void resetBrokenStreaks();
}