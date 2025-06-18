package com.sit708.mindmatters;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EmergencyActivity extends AppCompatActivity {

    private static final int REQUEST_SMS_PERMISSION = 101;
    private Button callNowButton;
    private FirebaseFirestore db;
    private String userId;
    private List<String> emergencyContactNumbers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);

        callNowButton = findViewById(R.id.callNowButton);
        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        fetchEmergencyContacts();

        callNowButton.setOnClickListener(v -> {
            callEmergencyNumber();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS}, REQUEST_SMS_PERMISSION);
            } else {
                sendSmsToContacts();
            }
        });
    }

    private void fetchEmergencyContacts() {
        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        List<Map<String, Object>> contactList = (List<Map<String, Object>>) snapshot.get("emergencyContacts");
                        if (contactList != null) {
                            for (Map<String, Object> contact : contactList) {
                                String phone = (String) contact.get("phone");
                                if (phone != null && !phone.isEmpty()) {
                                    emergencyContactNumbers.add(phone);
                                }
                            }
                        }
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to load emergency contacts", Toast.LENGTH_SHORT).show());
    }

    private void callEmergencyNumber() {
        String emergencyNumber = "000";
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + emergencyNumber));
        startActivity(intent);
    }

    private void sendSmsToContacts() {
        if (emergencyContactNumbers.isEmpty()) {
            Toast.makeText(this, "No emergency contacts found", Toast.LENGTH_SHORT).show();
            return;
        }

        SmsManager smsManager = SmsManager.getDefault();
        String message = "This is an emergency! The user needs immediate help.";

        for (String number : emergencyContactNumbers) {
            smsManager.sendTextMessage(number, null, message, null, null);
        }

        Toast.makeText(this, "SMS sent to emergency contacts", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_SMS_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendSmsToContacts();
            } else {
                Toast.makeText(this, "SMS permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
