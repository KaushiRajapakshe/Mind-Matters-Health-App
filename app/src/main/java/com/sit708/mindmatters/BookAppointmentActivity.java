package com.sit708.mindmatters;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.*;

public class BookAppointmentActivity extends AppCompatActivity {

    Spinner spinnerCategory;
    EditText etDate, etTime, etNotes;
    Button btnBook;
    Calendar calendar;

    FirebaseFirestore db;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_appointment);

        spinnerCategory = findViewById(R.id.spinnerCategory);
        etDate = findViewById(R.id.etDate);
        etTime = findViewById(R.id.etTime);
        etNotes = findViewById(R.id.etNotes);
        btnBook = findViewById(R.id.btnBookSession);

        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        calendar = Calendar.getInstance();

        setupSpinner();
        setupPickers();

        btnBook.setOnClickListener(v -> bookSession());
    }

    private void setupSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"Anxiety", "Stress", "Depression", "Sleep", "Grief"});
        spinnerCategory.setAdapter(adapter);
    }

    private void setupPickers() {
        etDate.setOnClickListener(v -> {
            new DatePickerDialog(this, (view, year, month, day) -> {
                etDate.setText((month + 1) + "/" + day + "/" + year);
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        etTime.setOnClickListener(v -> {
            new TimePickerDialog(this, (view, hour, minute) -> {
                etTime.setText(String.format("%02d:%02d", hour, minute));
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
        });
    }

    private void bookSession() {
        String category = spinnerCategory.getSelectedItem().toString();
        String date = etDate.getText().toString();
        String time = etTime.getText().toString();
        String notes = etNotes.getText().toString();

        if (date.isEmpty() || time.isEmpty()) {
            Toast.makeText(this, "Please select date and time", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> appointment = new HashMap<>();
        appointment.put("category", category);
        appointment.put("date", date);
        appointment.put("time", time);
        appointment.put("notes", notes);
        appointment.put("userId", userId);
        appointment.put("timestamp", new Date());

        db.collection("appointments")
                .add(appointment)
                .addOnSuccessListener(docRef -> {
                    Toast.makeText(this, "Appointment Booked", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, EditAppointmentActivity.class);
                    intent.putExtra("documentId", docRef.getId());
                    startActivity(intent);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
