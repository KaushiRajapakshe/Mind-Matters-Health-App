package com.sit708.mindmatters;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class NotificationsActivity extends AppCompatActivity {

    private ListView notificationList;
    private ArrayList<String> notifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        notificationList = findViewById(R.id.notificationList);

        notifications = new ArrayList<>();
        notifications.add("Logged in successfully.");
        notifications.add("Profile updated.");
        notifications.add("Booked appointment for 2PM.");
        notifications.add("Mind release session started.");
        notifications.add("Reminder: Your session is tomorrow at 3PM.");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, notifications);
        notificationList.setAdapter(adapter);
    }
}
