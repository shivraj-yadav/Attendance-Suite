package com.sohamkore.attendancesuite;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class QuizCategoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_category);

        // Hide default action bar as we're using custom toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Initialize CardViews
        CardView androidCard = findViewById(R.id.androidCard);
        CardView webDevCard = findViewById(R.id.webDevCard);

        // Set click listeners
        androidCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Android quiz activity
                Intent intent = new Intent(QuizCategoryActivity.this, AndroidQuizActivity.class);
                startActivity(intent);
            }
        });

        webDevCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle Web Development quiz selection
                // For now just show a toast until WebDevQuizActivity is implementedToast.makeText(QuizCategoryActivity.this, "Web Development Quiz coming soon!", Toast.LENGTH_SHORT).show();

                // Uncomment the code below when WebDevQuizActivity is implemented
                Intent intent = new Intent(QuizCategoryActivity.this, WebDevQuizActivity.class);
                 startActivity(intent);
            }
        });
    }
}