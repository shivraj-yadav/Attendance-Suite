package com.sohamkore.attendancesuite;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.Arrays;
import java.util.List;

public class StudentDashboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student_dashboard);
            uiSetup();
        drawerSetup();

        TextView name,id;
        name = findViewById(R.id.nameTxt);
        id = findViewById(R.id.teacherIdTxt);


        // Initialize SharedPreferences
        SharedPreferences sharedPreferences;
        sharedPreferences = getSharedPreferences("AttendanceSuite", MODE_PRIVATE);
        String studentId = sharedPreferences.getString("studentId", "id_not_found_contact_Admin");
        String studentName = sharedPreferences.getString("studentName", "name_not_found_contact_Admin");

        name.setText(""+studentName);
        id.setText("Student Id: "+studentId);
        CardView submitAttendance = findViewById(R.id.createStudentBtnLayout);
        submitAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(StudentDashboard.this,com.sohamkore.attendancesuite.submitAttendance.class);
                startActivity(i);
            }
        });
        CardView myAttendanceReport = findViewById(R.id.studentAttendanceBtn);
        myAttendanceReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(StudentDashboard.this,attendanceReport.class);
                startActivity(i);
            }
        });
        CardView parentBtn = findViewById(R.id.parentBtn);
        parentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Toast.makeText(StudentDashboard.this, "Parent creation from dashboard is DISABLED please use \"Login as Parent\"  page.", Toast.LENGTH_SHORT).show();

//                Intent i = new Intent(StudentDashboard.this,attendanceReport.class);
//                startActivity(i);
            }
        });
        CardView viewAlertsBtn = findViewById(R.id.viewAlertsBtn);
        viewAlertsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(StudentDashboard.this,viewAlerts.class);
                startActivity(i);
            }
        });
        CardView viewExamSchedules = findViewById(R.id.viewExamSchedule);
        viewExamSchedules.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(StudentDashboard.this,viewExamSchedules.class);
                startActivity(i);
            }
        });
        CardView quiz = findViewById(R.id.quiz);
        quiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(StudentDashboard.this, QuizCategoryActivity.class);
                startActivity(i);
            }
        });


        CardView chatboat = findViewById(R.id.chatboat);
        chatboat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(StudentDashboard.this, ChatBoatActivity.class);
                startActivity(i);
            }
        });



    }

    private void uiSetup() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    private void drawerSetup() {
        ConstraintLayout drawer =  findViewById(R.id.drawer);
        drawer.setVisibility(View.GONE);
        ImageView menuBtn = findViewById(R.id.menuBtn);
        menuBtn.setOnClickListener(v -> drawer.setVisibility(View.VISIBLE));

        ImageView drawerClose = findViewById(R.id.closeDrawer);
        drawerClose.setOnClickListener(v -> drawer.setVisibility(View.GONE));

        TextView homeDrawerBtn = findViewById(R.id.drawerHome);
        homeDrawerBtn.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(), Landing.class);
            startActivity(i);
        });

        TextView drawerContactBtn = findViewById(R.id.drawerContact);
        drawerContactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),contactUs.class);
                startActivity(i);
            }
        });
        TextView drawerAboutBtn = findViewById(R.id.drawerAbout);
        drawerAboutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),aboutUs.class);
                startActivity(i);
            }
        });

        TextView logoutBtn = findViewById(R.id.logoutBtn);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clear all shared preferences
                clearAllSharedPreferences();

                // Navigate to Login Activity
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish(); // Close the current activity
            }
        });

    }
    // List of all shared preference file names
    List<String> SHARED_PREF_FILES = Arrays.asList(
            "TeacherPrefs", "ParentPrefs", "AttendanceSuite" // Add all your shared preference file names here
    );
    private void clearAllSharedPreferences() {
        for (String prefFile : SHARED_PREF_FILES) {
            SharedPreferences sharedPreferences = getSharedPreferences(prefFile, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
        }
    }

}