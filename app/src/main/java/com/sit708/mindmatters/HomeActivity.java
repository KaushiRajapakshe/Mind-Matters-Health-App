package com.sit708.mindmatters;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {

    GridView featureGrid;
    EditText etSearch;

    String[] features = {
            "Mind Release", "Games", "Calm Mind", "Book Appointment", "Workout", "Medication",
            "Emergency", "View Appointments", "Clam Realm"
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
            R.drawable.calmrealm
    };

    List<String> filteredFeatures = new ArrayList<>();
    List<Integer> filteredIcons = new ArrayList<>();
    FeatureAdapter adapter;

    HashMap<String, Integer> activityMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        featureGrid = findViewById(R.id.featureGrid);
        etSearch = findViewById(R.id.etSearch);

        // Populate filtered data and map
        for (int i = 0; i < features.length; i++) {
            filteredFeatures.add(features[i]);
            filteredIcons.add(featureIcons[i]);
            activityMap.put(features[i], i);
        }

        adapter = new FeatureAdapter();
        featureGrid.setAdapter(adapter);

        // Search filter logic
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterFeatures(s.toString());
            }
        });

        featureGrid.setOnItemClickListener((adapterView, view, position, id) -> {
            String selected = filteredFeatures.get(position);
            launchActivity(activityMap.get(selected));
        });

        // Icons
        findViewById(R.id.btnNotifications).setOnClickListener(v ->
                startActivity(new Intent(this, NotificationsActivity.class)));

        findViewById(R.id.btnMenu).setOnClickListener(v ->
                startActivity(new Intent(this, ProfileActivity.class)));
    }

    private void filterFeatures(String keyword) {
        filteredFeatures.clear();
        filteredIcons.clear();
        keyword = keyword.toLowerCase(Locale.getDefault());

        for (int i = 0; i < features.length; i++) {
            if (features[i].toLowerCase().contains(keyword)) {
                filteredFeatures.add(features[i]);
                filteredIcons.add(featureIcons[i]);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void launchActivity(int position) {
        switch (position) {
            case 0: startActivity(new Intent(this, CalmMusicActivity.class)); break;
            case 1: startActivity(new Intent(this, GamesActivity.class)); break;
            case 2: startActivity(new Intent(this, CalmMindActivity.class)); break;
            case 3: startActivity(new Intent(this, BookAppointmentActivity.class)); break;
            case 4: startActivity(new Intent(this, WorkoutActivity.class)); break;
            case 5: startActivity(new Intent(this, MedicationActivity.class)); break;
            case 6: startActivity(new Intent(this, EmergencyActivity.class)); break;
            case 7: startActivity(new Intent(this, ViewAppointmentsActivity.class)); break;
            case 8: startActivity(new Intent(this, ChatActivity.class)); break;
        }
    }

    class FeatureAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return filteredFeatures.size();
        }

        @Override
        public Object getItem(int i) {
            return filteredFeatures.get(i);
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

            icon.setImageResource(filteredIcons.get(i));
            name.setText(filteredFeatures.get(i));
            return view;
        }
    }
}