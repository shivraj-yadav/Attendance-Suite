package com.sohamkore.attendancesuite;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Landing extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_landing);


//        startActivity(new Intent(Landing.this, teacher_absent_students.class));

        uiSetup();
//        Intent i = new Intent(Landing.this, datewiseAttendance.class);
//        startActivity(i);



        SharedPreferences teacherPref = getSharedPreferences("TeacherPrefs", MODE_PRIVATE);
        String teacherId = teacherPref.getString("teacherId", null);
        if (teacherId != null) {
            Toast.makeText(this, "Already logged in", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Landing.this, teacherDashboard.class));
            finish();
        }

        SharedPreferences sharedPreferences = getSharedPreferences("AttendanceSuite", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);


        if (isLoggedIn) {
            Intent intent = new Intent(Landing.this, StudentDashboard.class);
            startActivity(intent);
            finish();
            return;
        }

        // Initialize SharedPreferences

        sharedPreferences = getSharedPreferences("ParentPrefs", MODE_PRIVATE);

        // Check if the parent is already logged in
        if (sharedPreferences.getBoolean("isLoggedIn", false)) {
            redirectToDashboard();
            finish();
            return; // Skip registration screen
        }


        CardView studentCard, teacherCard, parentCard;

        studentCard = findViewById(R.id.cardView);
        teacherCard = findViewById(R.id.cardView2);
        parentCard = findViewById(R.id.cardView3);

        studentCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start MainActivity after delay
                startActivity(new Intent(Landing.this, StudentOnboard.class));
            }
        });

        teacherCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start MainActivity after delay
                startActivity(new Intent(Landing.this, TeacherRegistration.class));
            }
        });

        parentCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start MainActivity after delay
                startActivity(new Intent(Landing.this, parentRegistration.class));
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void uiSetup() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }
    private void redirectToDashboard() {
        Intent intent = new Intent(Landing.this, parentDashboard.class);
        startActivity(intent);
        finish(); // Close the Registration Activity
    }
}