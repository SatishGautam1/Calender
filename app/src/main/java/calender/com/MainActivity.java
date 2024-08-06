package calender.com;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private ListView reminderListView;
    private List<Map.Entry<String, String>> reminderList;
    private ReminderAdapter reminderAdapter;
    private int selectedYear, selectedMonth, selectedDay;
    private SharedPreferences sharedPreferences;
    private boolean isDateClickedOnce = false;
    private static final int DOUBLE_CLICK_DELAY = 300; // Milliseconds

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        calendarView = findViewById(R.id.calendar);
        reminderListView = findViewById(R.id.reminderListView);
        reminderList = new ArrayList<>();
        reminderAdapter = new ReminderAdapter(this, reminderList);
        reminderListView.setAdapter(reminderAdapter);
        sharedPreferences = getSharedPreferences("Reminders", MODE_PRIVATE);

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            if (isDateClickedOnce) {
                isDateClickedOnce = false;
                selectedYear = year;
                selectedMonth = month;
                selectedDay = dayOfMonth;
                loadRemindersForDate(selectedDay, selectedMonth, selectedYear);
                showReminderDialog();
            } else {
                isDateClickedOnce = true;
                new android.os.Handler().postDelayed(() -> isDateClickedOnce = false, DOUBLE_CLICK_DELAY);
            }
        });

        // Handle clicks on the TextView to refresh reminders
        TextView titleTextView = findViewById(R.id.title);
        titleTextView.setOnClickListener(this::onTitleClick);
    }

    // Method to handle clicks on the TextView to refresh reminders
    public void onTitleClick(View view) {
        loadAllReminders();
    }

    private void showReminderDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.reminder_dialog, null);
        EditText editTextReminder = dialogView.findViewById(R.id.editTextReminder);
        Button buttonSaveReminder = dialogView.findViewById(R.id.buttonSaveReminder);

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.show();

        buttonSaveReminder.setOnClickListener(v -> {
            String reminderText = editTextReminder.getText().toString().trim();
            if (!reminderText.isEmpty()) {
                String reminderDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                saveReminder(reminderDate, reminderText);
                loadAllReminders(); // Refresh the list view
                Toast.makeText(MainActivity.this, "Reminder set for " + reminderDate + ": " + reminderText, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } else {
                Toast.makeText(MainActivity.this, "Please enter a reminder", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveReminder(String date, String reminderText) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String allRemindersStr = sharedPreferences.getString("AllReminders", "");

        if (allRemindersStr.isEmpty()) {
            allRemindersStr = date + "|" + reminderText;
        } else {
            allRemindersStr += "," + date + "|" + reminderText;
        }

        editor.putString("AllReminders", allRemindersStr);
        editor.apply();
    }

    private void loadRemindersForDate(int day, int month, int year) {
        String date = day + "/" + (month + 1) + "/" + year;
        String allRemindersStr = sharedPreferences.getString("AllReminders", "");
        reminderList.clear();

        if (!allRemindersStr.isEmpty()) {
            String[] remindersArray = allRemindersStr.split(",");
            for (String reminder : remindersArray) {
                String[] parts = reminder.split("\\|");
                if (parts[0].equals(date)) {
                    reminderList.add(new HashMap.SimpleEntry<>(parts[0], parts[1]));
                }
            }
        }

        reminderAdapter.notifyDataSetChanged();
    }

    private void loadAllReminders() {
        String allRemindersStr = sharedPreferences.getString("AllReminders", "");
        reminderList.clear();

        if (!allRemindersStr.isEmpty()) {
            String[] remindersArray = allRemindersStr.split(",");
            for (String reminder : remindersArray) {
                String[] parts = reminder.split("\\|");
                reminderList.add(new HashMap.SimpleEntry<>(parts[0], parts[1]));
            }
        }

        reminderAdapter.notifyDataSetChanged();
    }
}
