package com.example.habit_traking;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.habit_traking.Habit;
import com.example.habit_traking.HabitDao;

@Database(entities = {Habit.class}, version = 2)
public abstract class HabitDatabase extends RoomDatabase {

    private static HabitDatabase instance;

    public abstract HabitDao habitDao();

    // Singleton pattern to prevent multiple databases opening
    public static synchronized HabitDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            HabitDatabase.class, "habit_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}