package com.sit708.mindmatters;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.util.*;

public class MedicationActivity extends AppCompatActivity {

    EditText etName, etDosage, etTime, etNotes;
    Button btnSave;
    ListView listView;
    ArrayAdapter<String> adapter;
    ArrayList<String> medicationList = new ArrayList<>();
    FirebaseFirestore db;
    String userId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medication);

        // Init views
        etName = findViewById(R.id.etName);
        etDosage = findViewById(R.id.etDosage);
        etTime = findViewById(R.id.etTime);
        etNotes = findViewById(R.id.etNotes);
        btnSave = findViewById(R.id.btnSaveMedication);
        listView = findViewById(R.id.listMedications);

        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, medicationList);
        listView.setAdapter(adapter);

        // Time picker
        etTime.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            new TimePickerDialog(this, (view, h, m) -> {
                String time = String.format(Locale.getDefault(), "%02d:%02d", h, m);
                etTime.setText(time);
            }, hour, minute, true).show();
        });

        btnSave.setOnClickListener(v -> saveMedication());

        fetchMedications();
    }

    private void saveMedication() {
        String name = etName.getText().toString().trim();
        String dosage = etDosage.getText().toString().trim();
        String time = etTime.getText().toString().trim();
        String notes = etNotes.getText().toString().trim();

        if (name.isEmpty() || dosage.isEmpty() || time.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("dosage", dosage);
        data.put("time", time);
        data.put("notes", notes);
        data.put("userId", userId);
        data.put("timestamp", new Date());

        db.collection("medications")
                .add(data)
                .addOnSuccessListener(docRef -> {
                    Toast.makeText(this, "Medication saved", Toast.LENGTH_SHORT).show();
                    clearInputs();
                    fetchMedications();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error saving: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void fetchMedications() {
        db.collection("medications")
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(query -> {
                    medicationList.clear();
                    for (DocumentSnapshot doc : query) {
                        String name = doc.getString("name");
                        String dosage = doc.getString("dosage");
                        String time = doc.getString("time");
                        String notes = doc.getString("notes");

                        medicationList.add(name + " - " + dosage + "\n" + time + " | " + notes);
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    private void clearInputs() {
        etName.setText("");
        etDosage.setText("");
        etTime.setText("");
        etNotes.setText("");
    }
}
