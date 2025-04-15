package com.sohamkore.attendancesuite;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.List;

public class teacherDashboard extends AppCompatActivity {

    private void uiSetup() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_teacher_dashboard);
        uiSetup();
        drawerSetup();
//        menuBtn2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(teacherDashboard.this, "ABC", Toast.LENGTH_SHORT).show();
//            }
//        });

        CardView createStudentBtn = findViewById(R.id.createStudentBtnLayout);
        CardView reportBtn = findViewById(R.id.reportBtn);
        CardView scheduleExam = findViewById(R.id.scheduleExamBtn);
        CardView createNotification = findViewById(R.id.createNotificationBtn);
        CardView studentListBtn = findViewById(R.id.studentAttendanceBtn);
        CardView dateWiseAttendanceBtn = findViewById(R.id.parentBtn);
        CardView generatePinBtn = findViewById(R.id.generatePinBtnCard);
        CardView absentStudentsBtn = findViewById(R.id.absentStudentsBtn);


         DatabaseReference teachersRef;
         SharedPreferences sharedPreferences;
        teachersRef = FirebaseDatabase.getInstance().getReference("teachers");
        sharedPreferences = getSharedPreferences("TeacherPrefs", MODE_PRIVATE);
        String teacherId = sharedPreferences.getString("teacherId", null);
        String teacherNameTxt = sharedPreferences.getString("teacherName", null);


        TextView teacherName = findViewById(R.id.nameTxt);
        TextView teacherIdTxt = findViewById(R.id.teacherIdTxt);
        teacherName.setText(teacherNameTxt);
        teacherIdTxt.setText("ID: "+teacherId);




        absentStudentsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(teacherDashboard.this, teacher_absent_students.class);
                startActivity(i);
            }
        });
        createStudentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(teacherDashboard.this, StudentOnboard.class);
                startActivity(i);
            }
        });
        generatePinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(teacherDashboard.this, GeneratePin.class);
                startActivity(i);
            }
        });

        dateWiseAttendanceBtn.setOnClickListener(view -> {
            // Handle the click event for Date Wise Attendance button
            Intent i = new Intent(teacherDashboard.this, datewiseAttendance.class);
            startActivity(i);
        });

        reportBtn.setOnClickListener(view -> {
            Intent i = new Intent(teacherDashboard.this, teacherReport.class);
            startActivity(i);
            // Handle the click event for Report button
        });

        scheduleExam.setOnClickListener(view -> {
            // Handle the click event for Schedule Exam button
            Intent i = new Intent(teacherDashboard.this, scheduleExam.class);
            startActivity(i);
        });

        createNotification.setOnClickListener(view -> {
            // Handle the click event for Create Notification button
            Intent i = new Intent(teacherDashboard.this, manageNotification.class);
            startActivity(i);
        });

        studentListBtn.setOnClickListener(view -> {
            // Handle the click event for Student List button
            Intent i = new Intent(teacherDashboard.this, studentList.class);

            startActivity(i);
        });


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