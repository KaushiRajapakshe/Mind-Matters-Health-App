package com.sit708.mindmatters;

import android.os.Bundle;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class WorkoutActivity extends AppCompatActivity {

    private WebView webView;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);

        webView = findViewById(R.id.webView);
        db = FirebaseFirestore.getInstance();

        setupWebView();
        loadVideoFromFirestore();
    }

    private void setupWebView() {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());
    }

    private void loadVideoFromFirestore() {
        db.collection("videos").document("workout").get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String url = doc.getString("url");
                        if (url != null && url.contains("youtu.be")) {
                            String videoId = extractVideoId(url);
                            loadYouTubeVideo(videoId);
                        } else {
                            Toast.makeText(this, "Invalid or missing workout video URL", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Workout video not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error fetching workout video", Toast.LENGTH_SHORT).show();
                    Log.e("WorkoutActivity", "Firestore error", e);
                });
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
