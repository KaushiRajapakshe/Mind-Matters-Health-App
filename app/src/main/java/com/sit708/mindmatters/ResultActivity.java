package com.sit708.mindmatters;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.util.*;

public class ResultActivity extends AppCompatActivity {

    LinearLayout resultContainer;
    TextView scoreText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        resultContainer = findViewById(R.id.resultContainer);
        scoreText = findViewById(R.id.scoreText);

        List<GamesActivity.QuizItem> quizData =
                (List<GamesActivity.QuizItem>) getIntent().getSerializableExtra("quizData");

        HashMap<Integer, String> userAnswers =
                (HashMap<Integer, String>) getIntent().getSerializableExtra("userAnswers");

        int score = 0;

        for (int i = 0; i < quizData.size(); i++) {
            GamesActivity.QuizItem item = quizData.get(i);
            String userAnswer = userAnswers.get(i);
            boolean correct = item.correctAnswer.equals(userAnswer);
            if (correct) score++;

            TextView qResult = new TextView(this);
            qResult.setText((i + 1) + ". " + item.question + "\nYour answer: " + userAnswer
                    + "\nCorrect: " + item.correctAnswer + (correct ? " ✅" : " ❌"));
            qResult.setPadding(0, 16, 0, 16);
            resultContainer.addView(qResult);
        }

        scoreText.setText("Score: " + score + "/" + quizData.size());
    }
}
