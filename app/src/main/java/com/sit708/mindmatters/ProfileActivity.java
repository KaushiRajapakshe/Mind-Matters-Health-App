package com.sit708.mindmatters;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.*;

import java.util.*;

public class ProfileActivity extends AppCompatActivity {

    private EditText nameInput, emailInput, bioInput;
    private LinearLayout contactContainer;
    private Button btnSave, btnAddContact;

    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    boolean emergencyStatus = false;

    private final List<View> contactViews = new ArrayList<>();  // Stores view blocks for removal

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        nameInput = findViewById(R.id.nameInput);
        emailInput = findViewById(R.id.emailInput);
        bioInput = findViewById(R.id.bioInput);
        contactContainer = findViewById(R.id.contactContainer);
        btnSave = findViewById(R.id.btnSave);
        btnAddContact = findViewById(R.id.btnAddContact);

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            loadUserProfile(currentUser.getUid());
        }

        btnAddContact.setOnClickListener(v -> addContactField(null, null));
        btnSave.setOnClickListener(v -> saveUserProfile());
    }

    private void addContactField(String contactName, String contactPhone) {
        View contactView = getLayoutInflater().inflate(R.layout.item_emergency_contact, null);

        EditText etName = contactView.findViewById(R.id.etContactName);
        EditText etPhone = contactView.findViewById(R.id.etContactPhone);
        ImageButton btnDelete = contactView.findViewById(R.id.btnDeleteContact);

        if (contactName != null) etName.setText(contactName);
        if (contactPhone != null) etPhone.setText(contactPhone);

        btnDelete.setOnClickListener(v -> {
            contactContainer.removeView(contactView);
            contactViews.remove(contactView);
        });

        contactViews.add(contactView);
        contactContainer.addView(contactView);
    }

    private void loadUserProfile(String userId) {
        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        nameInput.setText(doc.getString("fullName"));
                        emailInput.setText(doc.getString("email"));
                        bioInput.setText(doc.getString("bio"));

                        List<Map<String, String>> contacts = (List<Map<String, String>>) doc.get("emergencyContacts");
                        if (contacts != null) {
                            for (Map<String, String> contact : contacts) {
                                addContactField(contact.get("name"), contact.get("phone"));
                            }
                        }
                    }
                });
    }

    private void saveUserProfile() {
        String name = nameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String bio = bioInput.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Name and email are required", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Map<String, String>> contacts = new ArrayList<>();
        for (View view : contactViews) {
            EditText etName = view.findViewById(R.id.etContactName);
            EditText etPhone = view.findViewById(R.id.etContactPhone);
            String contactName = etName.getText().toString().trim();
            String contactPhone = etPhone.getText().toString().trim();

            if (!contactName.isEmpty() && !contactPhone.isEmpty()) {
                Map<String, String> map = new HashMap<>();
                map.put("name", contactName);
                map.put("phone", contactPhone);
                contacts.add(map);
            } else {
                emergencyStatus = true;
            }
        }

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("fullName", name);
        userMap.put("email", email);
        userMap.put("bio", bio);
        userMap.put("emergencyContacts", contacts);

        db.collection("users").document(currentUser.getUid())
                .set(userMap, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    if(emergencyStatus){
                        Toast.makeText(this, "Please add emergency contact", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show());
    }
}
