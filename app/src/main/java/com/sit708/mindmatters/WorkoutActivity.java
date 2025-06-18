package com.sit708.mindmatters;

import android.os.Bundle;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.*;

public class WorkoutActivity extends AppCompatActivity {

    private WebView webView;
    private FirebaseFirestore db;
    private String userId;

    private List<Map<String, Object>> videoList = new ArrayList<>();
    private List<String> videoKeys = new ArrayList<>();
    private int nextVideoIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);

        webView = findViewById(R.id.webView);
        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        setupWebView();
        loadVideosFromFirestore();
    }

    private void setupWebView() {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());
    }

    private void loadVideosFromFirestore() {
        db.collection("videos").document("workout").get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        Object rawVideoList = doc.get("videoList");

                        if (rawVideoList instanceof Map) {
                            videoList.clear();
                            videoKeys.clear();

                            Map<String, Object> videoMap = (Map<String, Object>) rawVideoList;

                            // Sort by numeric key
                            List<String> sortedKeys = new ArrayList<>(videoMap.keySet());
                            Collections.sort(sortedKeys, Comparator.comparingInt(Integer::parseInt));

                            for (String key : sortedKeys) {
                                Object item = videoMap.get(key);
                                if (item instanceof Map) {
                                    videoList.add((Map<String, Object>) item);
                                    videoKeys.add(key);
                                }
                            }

                            showNextUnwatchedVideo();
                        } else {
                            Toast.makeText(this, "Invalid videoList format", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Workout videos not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error fetching videos", Toast.LENGTH_SHORT).show();
                    Log.e("WorkoutActivity", "Firestore error", e);
                });
    }

    private void showNextUnwatchedVideo() {
        for (int i = 0; i < videoList.size(); i++) {
            Map<String, Object> video = videoList.get(i);
            List<String> watchedBy = (List<String>) video.get("watchedBy");

            if (watchedBy == null || !watchedBy.contains(userId)) {
                nextVideoIndex = i;
                String url = (String) video.get("url");

                if (url != null) {
                    String videoId = extractVideoId(url);
                    loadYouTubeVideo(videoId);
                    markVideoAsWatched(videoKeys.get(i));
                }
                return;
            }
        }

        Toast.makeText(this, "You've watched all workout videos!", Toast.LENGTH_LONG).show();
    }

    private void markVideoAsWatched(String key) {
        DocumentReference ref = db.collection("videos").document("workout");

        ref.update("videoList." + key + ".watchedBy", FieldValue.arrayUnion(userId))
                .addOnSuccessListener(aVoid -> Log.d("Workout", "Marked as watched"))
                .addOnFailureListener(e -> Log.e("Workout", "Failed to mark watched", e));
    }

    private void loadYouTubeVideo(String videoId) {
        String iframeHtml = "<html><body style='margin:0;padding:0;'>" +
                "<iframe width='100%' height='100%' " +
                "src='https://www.youtube.com/embed/" + videoId + "?autoplay=1&playsinline=1' " +
                "frameborder='0' allow='accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture' " +
                "allowfullscreen></iframe></body></html>";

        webView.loadDataWithBaseURL("https://www.youtube.com", iframeHtml, "text/html", "UTF-8", null);
    }

    private String extractVideoId(String url) {
        try {
            if (url.contains("v=")) {
                return url.split("v=")[1].split("&")[0];
            } else if (url.contains("youtu.be/")) {
                return url.substring(url.lastIndexOf("/") + 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
