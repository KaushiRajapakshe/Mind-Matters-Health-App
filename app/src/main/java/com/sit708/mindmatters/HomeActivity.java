package com.sit708.mindmatters;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    GridView featureGrid;
    String[] features = {
            "Mind Release", "Games", "Calm Mind", "Book Appointment", "Workout", "Medication",
            "Emergency", "View Appointments"
    };

    int[] featureIcons = {
            R.drawable.ab,
            R.drawable.kl,
            R.drawable.mn,
            R.drawable.gh,
            R.drawable.cd,
            R.drawable.ef,
            R.drawable.em,
            R.drawable.gh,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        featureGrid = findViewById(R.id.featureGrid);
        featureGrid.setAdapter(new FeatureAdapter());

        featureGrid.setOnItemClickListener((adapterView, view, position, id) -> {
            switch (position) {
                case 0: // Mind Release
                    startActivity(new Intent(this, CalmMusicActivity.class));
                    break;
                case 1: // Games
                    startActivity(new Intent(this, GamesActivity.class));
                    break;
                case 2: // Calm Mind
                    startActivity(new Intent(this, CalmMindActivity.class));
                    break;
                case 3: // Book Appointment
                    startActivity(new Intent(this, BookAppointmentActivity.class));
                    break;
                case 4: // Workout
                    startActivity(new Intent(this, WorkoutActivity.class));
                    break;
                case 5: // Medication (Chat)
                    startActivity(new Intent(this, ChatActivity.class));
                    break;
                case 6: // Emergency
                    startActivity(new Intent(this, EmergencyActivity.class));
                    break;
                case 7: // View Appointments
                    startActivity(new Intent(this, ViewAppointmentsActivity.class));
            }
        });

        // Notification & Profile
        findViewById(R.id.btnNotifications).setOnClickListener(v ->
                startActivity(new Intent(this, NotificationsActivity.class)));

        findViewById(R.id.btnMenu).setOnClickListener(v ->
                startActivity(new Intent(this, ProfileActivity.class)));
    }

    class FeatureAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return features.length;
        }

        @Override
        public Object getItem(int i) {
            return features[i];
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.feature_item, null);
            ImageView icon = view.findViewById(R.id.featureIcon);
            TextView name = view.findViewById(R.id.featureName);
            icon.setImageResource(featureIcons[i]);
            name.setText(features[i]);
            return view;
        }
    }
}