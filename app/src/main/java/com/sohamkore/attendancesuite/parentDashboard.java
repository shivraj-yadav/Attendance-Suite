package com.sohamkore.attendancesuite;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class parentDashboard extends AppCompatActivity {

    private TextView tvParentName, tvParentEmail, tvParentContact;
    private TextView tvStudentName, tvStudentEmail, tvStudentContact, tvStudentRollNo, tvStudentDepartment, tvStudentClass;
    private TextView tvAttendance;
    private Button btnLogout;
    private SharedPreferences sharedPreferences;
    private DatabaseReference studentsRef, attendanceRef;
    LinearLayout llNotifications, llExamSchedules;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_dashboard);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("ParentPrefs", MODE_PRIVATE);

        // Initialize Views
        llNotifications = findViewById(R.id.llNotifications);
        llExamSchedules = findViewById(R.id.llExamSchedules);

        // Fetch and display notifications and exam schedules
        loadNotifications();
        loadExamSchedules();

        // Initialize Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        studentsRef = database.getReference("students");
        attendanceRef = database.getReference("attendance_records");

        // Initialize Views
        tvParentName = findViewById(R.id.tvParentName);
        tvParentEmail = findViewById(R.id.tvParentEmail);
        tvParentContact = findViewById(R.id.tvParentContact);
        tvStudentName = findViewById(R.id.tvStudentName);
        tvStudentEmail = findViewById(R.id.tvStudentEmail);
        tvStudentContact = findViewById(R.id.tvStudentContact);
        tvStudentRollNo = findViewById(R.id.tvStudentRollNo);
        tvStudentDepartment = findViewById(R.id.tvStudentDepartment);
        tvStudentClass = findViewById(R.id.tvStudentClass);
        tvAttendance = findViewById(R.id.tvAttendance);
        btnLogout = findViewById(R.id.btnLogout);

        // Load Parent and Student Data
        loadParentData();
        loadStudentData();
        loadAttendanceData();

        // Logout Button
        btnLogout.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear(); // Clear all saved data
            editor.apply();

            Toast.makeText(parentDashboard.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(parentDashboard.this, Landing.class));
            finish(); // Close the Dashboard Activity
        });
    }

    private void loadParentData() {
        String parentName = sharedPreferences.getString("parentName", "N/A");
        String parentEmail = sharedPreferences.getString("parentEmail", "N/A");
        String parentContact = sharedPreferences.getString("parentContact", "N/A");

        tvParentName.setText("Name: " + parentName);
        tvParentEmail.setText("Email: " + parentEmail);
        tvParentContact.setText("Contact: " + parentContact);
    }

    private void loadStudentData() {
        String studentId = sharedPreferences.getString("studentId", null);
        if (studentId != null) {
            studentsRef.child(studentId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String name = snapshot.child("name").getValue(String.class);
                        String email = snapshot.child("email").getValue(String.class);
                        String contact = snapshot.child("contact").getValue(String.class);
                        String rollNo = snapshot.child("rollNo").getValue(String.class);
                        String department = snapshot.child("department").getValue(String.class);
                        String studentClass = snapshot.child("studentClass").getValue(String.class);

                        tvStudentName.setText("Name: " + name);
                        tvStudentEmail.setText("Email: " + email);
                        tvStudentContact.setText("Contact: " + contact);
                        tvStudentRollNo.setText("Roll No: " + rollNo);
                        tvStudentDepartment.setText("Department: " + department);
                        tvStudentClass.setText("Class: " + studentClass);
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Toast.makeText(parentDashboard.this, "Failed to load student data", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void loadAttendanceData() {
        String studentId = sharedPreferences.getString("studentId", null);
        if (studentId != null) {
            attendanceRef.child(studentId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        long totalDays = snapshot.getChildrenCount();
                        long presentDays = 0;

                        // Manually iterate over the children to count present days
                        for (DataSnapshot child : snapshot.getChildren()) {
                            Boolean isPresent = child.getValue(Boolean.class);
                            if (isPresent != null && isPresent) {
                                presentDays++;
                            }
                        }

                        long absentDays = totalDays - presentDays;

                        tvAttendance.setText("Attendance: " + presentDays + " / " + totalDays +
                                " (Present: " + presentDays + ", Absent: " + absentDays + ")");
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Toast.makeText(parentDashboard.this, "Failed to load attendance data", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    private void loadNotifications() {
        DatabaseReference notificationsRef = FirebaseDatabase.getInstance().getReference("notifications");
        notificationsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                llNotifications.removeAllViews(); // Clear existing views
                for (DataSnapshot notificationSnapshot : snapshot.getChildren()) {
                    String from = notificationSnapshot.child("from").getValue(String.class);
                    String subject = notificationSnapshot.child("subject").getValue(String.class);
                    String message = notificationSnapshot.child("message").getValue(String.class);

                    // Retrieve the timestamp as a String, then parse it to long
                    String timestampStr = notificationSnapshot.child("timestamp").getValue(String.class);
                    Long timestamp = null;
                    if (timestampStr != null) {
                        try {
                            timestamp = Long.parseLong(timestampStr);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }

                    // Convert timestamp to readable format
                    String formattedTime = "N/A";
                    if (timestamp != null) {
                        formattedTime = new java.text.SimpleDateFormat("dd/MM/yyyy hh:mm a")
                                .format(new java.util.Date(timestamp));
                    }

                    // Create a TextView for each notification
                    TextView tvNotification = new TextView(parentDashboard.this);
                    tvNotification.setText(String.format("From: %s\nSubject: %s\nMessage: %s\nTime: %s\n",
                            from, subject, message, formattedTime));
                    tvNotification.setTextSize(14);
                    tvNotification.setTextColor(Color.BLACK);
                    tvNotification.setTypeface(ResourcesCompat.getFont(parentDashboard.this, R.font.poppins_regular));
                    tvNotification.setPadding(0, 8, 0, 8);

                    llNotifications.addView(tvNotification);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(parentDashboard.this, "Failed to load notifications", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void loadExamSchedules() {
        DatabaseReference examsRef = FirebaseDatabase.getInstance().getReference("exams");
        examsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                llExamSchedules.removeAllViews(); // Clear existing views
                for (DataSnapshot examSnapshot : snapshot.getChildren()) {
                    String courseCode = examSnapshot.child("courseCode").getValue(String.class);
                    String courseTitle = examSnapshot.child("courseTitle").getValue(String.class);
                    String examDate = examSnapshot.child("examDate").getValue(String.class);
                    String startTime = examSnapshot.child("startTime").getValue(String.class);
                    String endTime = examSnapshot.child("endTime").getValue(String.class);

                    // Create a TextView for each exam
                    TextView tvExam = new TextView(parentDashboard.this);
                    tvExam.setText(String.format("Course: %s (%s)\nDate: %s\nTime: %s - %s\n", courseTitle, courseCode, examDate, startTime, endTime));
                    tvExam.setTextSize(14);
                    tvExam.setTextColor(Color.BLACK);
                    tvExam.setTypeface(ResourcesCompat.getFont(parentDashboard.this, R.font.poppins_regular));
                    tvExam.setPadding(0, 8, 0, 8);

                    llExamSchedules.addView(tvExam);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(parentDashboard.this, "Failed to load exam schedules", Toast.LENGTH_SHORT).show();
            }
        });
    }
}