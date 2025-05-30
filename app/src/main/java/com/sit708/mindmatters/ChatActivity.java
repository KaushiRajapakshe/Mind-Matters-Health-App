package com.sit708.mindmatters;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.android.volley.Request;
import com.android.volley.RequestQueue;

import java.io.InputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerViewChat;
    private EditText editMessage;
    private ImageButton btnSend;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatMessages = new ArrayList<>();
    private List<PredefinedResponse> predefinedResponses;
    private String username = "Guest";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        if (getIntent().hasExtra("username")) {
            username = getIntent().getStringExtra("username");
        }

        recyclerViewChat = findViewById(R.id.recyclerViewChat);
        editMessage = findViewById(R.id.messageEditText);
        btnSend = findViewById(R.id.sendButton);

        recyclerViewChat.setLayoutManager(new LinearLayoutManager(this));
        chatAdapter = new ChatAdapter(chatMessages);
        recyclerViewChat.setAdapter(chatAdapter);

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://10.0.2.2:5000/getQuiz?topic=Comic"; // use 10.0.2.2 for localhost in Android emulator

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    parseQuizJson(response);
                    showQuestion(currentIndex);
                },
                error -> Toast.makeText(this, "Failed to load quiz", Toast.LENGTH_SHORT).show());

        queue.add(stringRequest);

        addBotMessage("Hi " + username + "! How can I help you today?");

        btnSend.setOnClickListener(v -> sendMessage());
    }

    private void sendMessage() {
        String message = editMessage.getText().toString().trim();
        if (message.isEmpty()) return;

        chatMessages.add(new ChatMessage(message, true));
        chatAdapter.notifyItemInserted(chatMessages.size() - 1);
        recyclerViewChat.scrollToPosition(chatMessages.size() - 1);
        editMessage.setText("");

        for (PredefinedResponse item : predefinedResponses) {
            if (item.getUserMessage().equalsIgnoreCase(message)) {
                addBotMessage(item.getResponse());
                return;
            }
        }

        addBotMessage("Sorry, I don't have a response for that yet.");
    }

    private void addBotMessage(String msg) {
        chatMessages.add(new ChatMessage(msg, false));
        chatAdapter.notifyItemInserted(chatMessages.size() - 1);
        recyclerViewChat.scrollToPosition(chatMessages.size() - 1);
    }

    private void loadPredefinedResponses() {
        try {
            InputStream is = getResources().openRawResource(R.raw.predefined_responses);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            String json = new String(buffer, "UTF-8");

            Gson gson = new Gson();
            Type listType = new TypeToken<List<PredefinedResponse>>() {}.getType();
            predefinedResponses = gson.fromJson(json, listType);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to load chatbot responses", Toast.LENGTH_SHORT).show();
            predefinedResponses = new ArrayList<>();
        }
    }
}
