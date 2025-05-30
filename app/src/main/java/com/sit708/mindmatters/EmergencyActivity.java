package com.sit708.mindmatters;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class EmergencyActivity extends AppCompatActivity {

    private Button callNowButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);

        callNowButton = findViewById(R.id.callNowButton);

        callNowButton.setOnClickListener(v -> {
            // Open phone dialer with emergency number
            String emergencyNumber = "000";
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + emergencyNumber));
            startActivity(intent);
        });
    }
}
