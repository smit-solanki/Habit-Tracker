package com.example.habit_traking;

import android.content.Context;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
//import com.yourname.habitapp.database.Habit;
//import com.yourname.habitapp.database.HabitDatabase;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class HabitAdapter extends RecyclerView.Adapter<HabitAdapter.HabitViewHolder> {

    private List<Habit> habitList = new ArrayList<>();
    private Context context;

    public HabitAdapter(Context context) {
        this.context = context;
    }

    public void setHabits(List<Habit> habits) {
        this.habitList = habits;
        notifyDataSetChanged();
    }
//    public Habit getHabitAt(int position) {
//        return habitList.get(position);
//    }

    @NonNull
    @Override
    public HabitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_habit, parent, false);
        return new HabitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HabitViewHolder holder, int position) {
        Habit currentHabit = habitList.get(position);
        holder.tvTitle.setText(currentHabit.getTitle());
        holder.tvStreak.setText("🔥 " + currentHabit.getStreakCount() + " Day Streak");

        // Prevents checkbox from triggering its own listener during binding
        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(currentHabit.isCompletedToday());

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // 1. Visual Feedback & Haptics
            buttonView.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);

            // 2. Update Local Object
            currentHabit.setCompletedToday(isChecked);

            if (isChecked) {
                currentHabit.setStreakCount(currentHabit.getStreakCount() + 1);
            } else {
                currentHabit.setStreakCount(Math.max(0, currentHabit.getStreakCount() - 1));
            }

            // 3. Update Database in Background
            Executors.newSingleThreadExecutor().execute(() -> {
                HabitDatabase.getInstance(context).habitDao().update(currentHabit);
            });

            new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                // This ensures the CheckBox stays checked and the UI is locked in
                notifyItemChanged(holder.getAdapterPosition());
            });
//            holder.tvStreak.setText("🔥 " + currentHabit.getStreakCount() + " Day Streak");
        });
        // DELETE BUTTON LOGIC
        holder.btnDelete.setOnClickListener(v -> {
            new android.app.AlertDialog.Builder(context)
                    .setTitle("Delete Habit")
                    .setMessage("Are you sure you want to delete this habit?")
                    .setPositiveButton("Delete", (dialog, which) -> {

                        // Delete from Database in background
                        Executors.newSingleThreadExecutor().execute(() -> {
                            HabitDatabase.getInstance(context).habitDao().deleteHabit(currentHabit);
                        });

                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return habitList.size();
    }

    static class HabitViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvStreak;
        CheckBox checkBox;
        ImageButton btnDelete;

        public HabitViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvHabitTitle);
            tvStreak = itemView.findViewById(R.id.tvStreak);
            checkBox = itemView.findViewById(R.id.checkDone);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
