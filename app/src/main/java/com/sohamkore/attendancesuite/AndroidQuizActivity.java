package com.sohamkore.attendancesuite;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AndroidQuizActivity extends AppCompatActivity {

    TextView questionText;
    RadioGroup optionsGroup;
    RadioButton option1, option2, option3, option4;
    Button submitBtn;

    List<Question> questionList;
    int currentIndex = 0;
    int score = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_quiz);

        questionText = findViewById(R.id.questionText);
        optionsGroup = findViewById(R.id.optionsGroup);
        option1 = findViewById(R.id.option1);
        option2 = findViewById(R.id.option2);
        option3 = findViewById(R.id.option3);
        option4 = findViewById(R.id.option4);
        submitBtn = findViewById(R.id.submitBtn);

        questionList = getQuestions();
        Collections.shuffle(questionList); // Shuffle the list

        loadQuestion();

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = optionsGroup.getCheckedRadioButtonId();
                if (selectedId == -1) {
                    Toast.makeText(AndroidQuizActivity.this, "Please select an answer", Toast.LENGTH_SHORT).show();
                    return;
                }

                RadioButton selectedOption = findViewById(selectedId);
                String selectedAnswer = selectedOption.getText().toString();

                if (selectedAnswer.equals(questionList.get(currentIndex).getCorrectAnswer())) {
                    score++;
                }

                currentIndex++;
                if (currentIndex < questionList.size()) {
                    loadQuestion();
                } else {
                    Toast.makeText(AndroidQuizActivity.this, "Quiz Finished! Your score: " + score + "/10", Toast.LENGTH_LONG).show();
                    finish(); // Or navigate to result activity
                }
            }
        });
    }

    private void loadQuestion() {
        optionsGroup.clearCheck();
        Question currentQuestion = questionList.get(currentIndex);
        questionText.setText((currentIndex + 1) + ". " + currentQuestion.getQuestionText());
        List<String> shuffledOptions = new ArrayList<>(currentQuestion.getOptions());
        Collections.shuffle(shuffledOptions);
        option1.setText(shuffledOptions.get(0));
        option2.setText(shuffledOptions.get(1));
        option3.setText(shuffledOptions.get(2));
        option4.setText(shuffledOptions.get(3));
    }

    private List<Question> getQuestions() {
        List<Question> list = new ArrayList<>();
        list.add(new Question("What is Android?", List.of("Operating System", "Web Browser", "Database", "Compiler"), "Operating System"));
        list.add(new Question("Who developed Android?", List.of("Google", "Apple", "Microsoft", "Facebook"), "Google"));
        list.add(new Question("Which language is used for Android development?", List.of("Kotlin", "Swift", "JavaScript", "Python"), "Kotlin"));
        list.add(new Question("What is an Activity in Android?", List.of("Single screen UI", "Database", "Hardware", "Compiler"), "Single screen UI"));
        list.add(new Question("Which file contains layout XML?", List.of("res/layout", "src/java", "manifest", "drawable"), "res/layout"));
        list.add(new Question("What does APK stand for?", List.of("Android Package", "Android Phone Kit", "App Package Kit", "Android Private Key"), "Android Package"));
        list.add(new Question("Which tool is used to develop Android apps?", List.of("Android Studio", "Eclipse", "Photoshop", "PyCharm"), "Android Studio"));
        list.add(new Question("Which component is NOT part of Android?", List.of("Intent", "Service", "Fragment", "Router"), "Router"));
        list.add(new Question("Which method is called when Activity starts?", List.of("onCreate()", "start()", "main()", "run()"), "onCreate()"));
        list.add(new Question("Which permission is needed for Internet?", List.of("INTERNET", "ACCESS_WIFI", "READ_PHONE", "CAMERA"), "INTERNET"));
        return list;
    }
}
