package com.sit708.mindmatters;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.util.*;

public class ViewAppointmentsActivity extends AppCompatActivity {

    ListView listView;
    List<Map<String, Object>> appointmentList = new ArrayList<>();
    List<String> docIds = new ArrayList<>();
    ArrayAdapter<String> adapter;
    List<String> displayList = new ArrayList<>();

    FirebaseFirestore db;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_appointments);

        listView = findViewById(R.id.listAppointments);

        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, displayList);
        listView.setAdapter(adapter);

        fetchAppointments();

        listView.setOnItemClickListener((parent, view, position, id) -> {
            String docId = docIds.get(position);
            Intent intent = new Intent(this, EditAppointmentActivity.class);
            intent.putExtra("documentId", docId);
            startActivity(intent);
        });
    }

    private void fetchAppointments() {
        System.out.println("asdfghjkl;" +userId);
        db.collection("appointments")
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    appointmentList.clear();
                    docIds.clear();
                    displayList.clear();

                    for (DocumentSnapshot doc : querySnapshot) {
                        Map<String, Object> data = doc.getData();
                        if (data == null) continue;

                        String category = (String) data.get("category");
                        String date = (String) data.get("date");
                        String time = (String) data.get("time");
                        String notes = (String) data.get("notes");

                        String display = String.format(Locale.getDefault(),
                                "%s | %s %s\n%s",
                                category != null ? category : "N/A",
                                date != null ? date : "--",
                                time != null ? time : "--",
                                notes != null ? notes : "");
                        displayList.add(display);
                        docIds.add(doc.getId());
                        appointmentList.add(data);
                    }

                    if (displayList.isEmpty()) {
                        displayList.add("No appointments found.");
                    }

                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to load appointments", Toast.LENGTH_SHORT).show());
    }

}
