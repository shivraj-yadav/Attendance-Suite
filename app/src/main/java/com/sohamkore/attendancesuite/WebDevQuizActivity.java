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

public class WebDevQuizActivity extends AppCompatActivity {

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

        questionList = getWebDevQuestions();
        Collections.shuffle(questionList); // Shuffle question order

        loadQuestion();

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = optionsGroup.getCheckedRadioButtonId();
                if (selectedId == -1) {
                    Toast.makeText(WebDevQuizActivity.this, "Please select an answer", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(WebDevQuizActivity.this, "Quiz Finished! Your score: " + score + "/10", Toast.LENGTH_LONG).show();
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

    private List<Question> getWebDevQuestions() {
        List<Question> list = new ArrayList<>();
        list.add(new Question("What does HTML stand for?", List.of("Hyper Text Markup Language", "Home Tool Markup Language", "Hyperlinks and Text Markup Language", "Hyper Tool Machine Language"), "Hyper Text Markup Language"));
        list.add(new Question("Which tag is used to create a hyperlink in HTML?", List.of("<a>", "<link>", "<href>", "<url>"), "<a>"));
        list.add(new Question("What does CSS stand for?", List.of("Cascading Style Sheets", "Creative Style Sheet", "Computer Style Sheet", "Colorful Style Sheet"), "Cascading Style Sheets"));
        list.add(new Question("Which HTML attribute is used to define inline styles?", List.of("style", "class", "font", "styles"), "style"));
        list.add(new Question("Which property is used to change text color in CSS?", List.of("color", "font-color", "text-color", "text-style"), "color"));
        list.add(new Question("Which JavaScript keyword is used to declare a variable?", List.of("var", "int", "String", "float"), "var"));
        list.add(new Question("Which symbol is used for comments in JavaScript?", List.of("//", "<!-- -->", "/* */", "#"), "//"));
        list.add(new Question("Which method is used to display a message box in JS?", List.of("alert()", "prompt()", "confirm()", "msg()"), "alert()"));
        list.add(new Question("Which HTML tag is used to display an image?", List.of("<img>", "<picture>", "<src>", "<image>"), "<img>"));
        list.add(new Question("Which HTML5 element is used to define navigation links?", List.of("<nav>", "<navigate>", "<menu>", "<links>"), "<nav>"));
        list.add(new Question("Which language runs in a web browser?", List.of("JavaScript", "C++", "Python", "Java"), "JavaScript"));
        list.add(new Question("What does the DOM stand for?", List.of("Document Object Model", "Digital Object Model", "Design Order Model", "Document Oriented Model"), "Document Object Model"));
        list.add(new Question("How do you write a function in JS?", List.of("function myFunc()", "def myFunc()", "func myFunc()", "function:myFunc()"), "function myFunc()"));
        list.add(new Question("What is Bootstrap used for?", List.of("Responsive Design", "Database", "Hosting", "APIs"), "Responsive Design"));
        list.add(new Question("Which protocol is used to send webpages?", List.of("HTTP", "FTP", "SMTP", "TCP"), "HTTP"));
        Collections.shuffle(list);
        return list.subList(0, 10);

    }
}
