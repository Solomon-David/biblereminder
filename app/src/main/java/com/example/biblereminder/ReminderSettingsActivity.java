package com.example.biblereminder;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ReminderSettingsActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "bible_channel";
    private static final int NOTIFICATION_REQUEST_CODE = 101;

    private TimePicker timePicker;
    private Button saveReminderButton;
    private Button cancelReminderButton;
    private TextView existingReminderView;

    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(this, ReminderReceiver.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return PendingIntent.getBroadcast(
                this, 0, intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_settings);

        timePicker = findViewById(R.id.timePicker);
        saveReminderButton = findViewById(R.id.saveReminderButton);
        cancelReminderButton = findViewById(R.id.cancelReminderButton);
        existingReminderView = findViewById(R.id.existingReminderView);

        createNotificationChannel();
        requestNotificationPermission();

        saveReminderButton.setOnClickListener(v -> scheduleRepeatingReminder());
        cancelReminderButton.setOnClickListener(v -> cancelReminder());

        displayExistingReminder();
    }

    private void scheduleRepeatingReminder() {
        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY,
                    getPendingIntent()
            );
            Toast.makeText(this, "Daily repeating reminder set!", Toast.LENGTH_SHORT).show();
            displayExistingReminder();
        } else {
            Toast.makeText(this, "Failed to access AlarmManager", Toast.LENGTH_SHORT).show();
        }
    }

    private void cancelReminder() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(getPendingIntent());
            Toast.makeText(this, "Reminder canceled", Toast.LENGTH_SHORT).show();
            existingReminderView.setText("No reminder set");
        }
    }

    private void displayExistingReminder() {
        Calendar now = Calendar.getInstance();
        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();

        Calendar reminderTime = Calendar.getInstance();
        reminderTime.set(Calendar.HOUR_OF_DAY, hour);
        reminderTime.set(Calendar.MINUTE, minute);

        if (reminderTime.before(now)) {
            reminderTime.add(Calendar.DAY_OF_YEAR, 1);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        existingReminderView.setText("Next reminder at: " + sdf.format(reminderTime.getTime()));
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Bible Reminder Channel";
            String description = "Channel for daily Bible reading reminders";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.enableVibration(true);

            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            channel.setSound(alarmSound, audioAttributes);

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_REQUEST_CODE
                );
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == NOTIFICATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
