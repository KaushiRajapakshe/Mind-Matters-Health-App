package com.sit708.mindmatters;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.*;

import java.util.*;

public class EditAppointmentActivity extends AppCompatActivity {

    private EditText etDate, etTime, etNotes;
    private Spinner spinnerCategory;
    private Button btnUpdate;

    private FirebaseFirestore db;
    private String docId;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_appointment);

        // UI References
        etDate = findViewById(R.id.etDate);
        etTime = findViewById(R.id.etTime);
        etNotes = findViewById(R.id.etNotes);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        btnUpdate = findViewById(R.id.btnUpdate);

        db = FirebaseFirestore.getInstance();
        calendar = Calendar.getInstance();

        docId = getIntent().getStringExtra("documentId");

        setupSpinners();
        setupPickers();

        if (docId != null) {
            loadAppointment();
        }

        btnUpdate.setOnClickListener(v -> updateAppointment());
    }

    private void setupSpinners() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"Anxiety", "Stress", "Depression", "Sleep", "Grief"});
        spinnerCategory.setAdapter(adapter);
    }

    private void setupPickers() {
        etDate.setOnClickListener(v -> new DatePickerDialog(this, (view, year, month, day) ->
                etDate.setText((month + 1) + "/" + day + "/" + year),
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show());

        etTime.setOnClickListener(v -> new TimePickerDialog(this, (view, hour, minute) ->
                etTime.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute)),
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true).show());
    }

    private void loadAppointment() {
        db.collection("appointments").document(docId).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        etDate.setText(doc.getString("date"));
                        etTime.setText(doc.getString("time"));
                        etNotes.setText(doc.getString("notes"));

                        String category = doc.getString("category");
                        if (category != null) {
                            ArrayAdapter adapter = (ArrayAdapter) spinnerCategory.getAdapter();
                            int pos = adapter.getPosition(category);
                            if (pos >= 0) spinnerCategory.setSelection(pos);
                        }
                    } else {
                        Toast.makeText(this, "Appointment not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to load appointment", Toast.LENGTH_SHORT).show());
    }

    private void updateAppointment() {
        String category = spinnerCategory.getSelectedItem().toString();
        String date = etDate.getText().toString().trim();
        String time = etTime.getText().toString().trim();
        String notes = etNotes.getText().toString().trim();

        if (date.isEmpty() || time.isEmpty()) {
            Toast.makeText(this, "Date and Time are required", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("category", category);
        updateMap.put("date", date);
        updateMap.put("time", time);
        updateMap.put("notes", notes);
        updateMap.put("timestamp", new Timestamp(new Date()));  // Update timestamp

        db.collection("appointments").document(docId)
                .update(updateMap)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Appointment updated", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, HomeActivity.class);
                    startActivity(intent);
                })
                        .addOnFailureListener(e ->
                                Toast.makeText(this, "Error updating: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
