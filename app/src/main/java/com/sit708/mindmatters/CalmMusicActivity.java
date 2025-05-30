package com.sit708.mindmatters;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class CalmMusicActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private Button playPauseButton;
    private boolean isPlaying = false;
    private String musicUrl; // Firebase Storage URL

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calm_music);

        TextView titleText = findViewById(R.id.titleText);
        playPauseButton = findViewById(R.id.playPauseButton);

        // 1. Get the Firebase Storage reference (e.g. /music/relaxing.mp3)
        StorageReference storageRef = FirebaseStorage.getInstance()
                .getReference().child("music/relaxing.mp3");

        // 2. Get the download URL from Firebase Storage
        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            musicUrl = uri.toString();

            // 3. Prepare the MediaPlayer with the URL (stream)
            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(musicUrl);
                mediaPlayer.prepareAsync();
                mediaPlayer.setOnPreparedListener(mp -> playPauseButton.setEnabled(true));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).addOnFailureListener(e -> {
            // Handle errors
            playPauseButton.setEnabled(false);
        });

        playPauseButton.setEnabled(false);
        playPauseButton.setOnClickListener(v -> {
            if (mediaPlayer == null) return;
            if (isPlaying) {
                mediaPlayer.pause();
                playPauseButton.setText("Play");
                isPlaying = false;
            } else {
                mediaPlayer.start();
                playPauseButton.setText("Pause");
                isPlaying = true;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
