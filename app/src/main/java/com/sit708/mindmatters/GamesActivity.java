package com.sit708.mindmatters;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.Serializable;
import java.util.*;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class GamesActivity extends AppCompatActivity {

    private List<QuizItem> quizList = new ArrayList<>();
    private int currentIndex = 0;

    private TextView questionText;
    private RadioGroup optionsGroup;
    private Button nextButton;

    private Map<Integer, String> userAnswers = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_games);

        questionText = findViewById(R.id.questionText);
        optionsGroup = findViewById(R.id.optionsGroup);
        nextButton = findViewById(R.id.nextButton);

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://10.0.2.2:5000/getQuiz?topic=Comic"; // use 10.0.2.2 for localhost in Android emulator

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    parseQuizJson(response);
                    showQuestion(currentIndex);
                },
                error -> Toast.makeText(this, "Failed to load quiz", Toast.LENGTH_SHORT).show());

        queue.add(stringRequest);

//        loadJsonFromRaw();
        showQuestion(currentIndex);

        nextButton.setOnClickListener(v -> {
            int selectedId = optionsGroup.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(this, "Please select an answer", Toast.LENGTH_SHORT).show();
                return;
            }

            RadioButton selectedRadio = findViewById(selectedId);
            String selectedAnswer = selectedRadio.getText().toString();
            userAnswers.put(currentIndex, selectedAnswer);

            if (currentIndex < quizList.size() - 1) {
                currentIndex++;
                showQuestion(currentIndex);
            } else {
                Intent intent = new Intent(GamesActivity.this, ResultActivity.class);
                intent.putExtra("quizData", new ArrayList<>(quizList));
                intent.putExtra("userAnswers", (HashMap<Integer, String>) userAnswers);
                startActivity(intent);
                finish();
            }
        });
    }

    private void parseQuizJson(String jsonString) {
        try {
            JSONObject obj = new JSONObject(jsonString);
            JSONArray quizArray = obj.getJSONArray("quiz");

            for (int i = 0; i < quizArray.length(); i++) {
                JSONObject quizObj = quizArray.getJSONObject(i);
                String question = quizObj.getString("question");
                String correct = quizObj.getString("correct_answer");
                JSONArray optionsJson = quizObj.getJSONArray("options");

                List<String> options = new ArrayList<>();
                for (int j = 0; j < optionsJson.length(); j++) {
                    options.add(optionsJson.getString(j));
                }

                quizList.add(new QuizItem(question, correct, options));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadJsonFromRaw() {
        try {
            InputStream is = getResources().openRawResource(R.raw.quiz);
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();

            String json = new String(buffer, "UTF-8");
            JSONArray jsonArray = new JSONArray(json);
            JSONObject selected = jsonArray.getJSONObject(new Random().nextInt(jsonArray.length()));
            JSONArray quizArray = selected.getJSONArray("quiz");

            for (int i = 0; i < quizArray.length(); i++) {
                JSONObject obj = quizArray.getJSONObject(i);
                String question = obj.getString("question");
                String correct = obj.getString("correct_answer");
                JSONArray optionsJson = obj.getJSONArray("options");

                List<String> options = new ArrayList<>();
                for (int j = 0; j < optionsJson.length(); j++) {
                    options.add(optionsJson.getString(j));
                }

                quizList.add(new QuizItem(question, correct, options));
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to load quiz", Toast.LENGTH_SHORT).show();
        }
    }

    private void showQuestion(int index) {
        QuizItem item = quizList.get(index);
        questionText.setText(item.question);
        optionsGroup.removeAllViews();

        for (String option : item.options) {
            RadioButton rb = new RadioButton(this);
            rb.setText(option);
            rb.setTextSize(18f);
            optionsGroup.addView(rb);
        }
    }

    public static class QuizItem implements Serializable {
        public String question;
        public String correctAnswer;
        public List<String> options;

        public QuizItem(String question, String correctAnswer, List<String> options) {
            this.question = question;
            this.correctAnswer = correctAnswer;
            this.options = options;
        }
    }
}
