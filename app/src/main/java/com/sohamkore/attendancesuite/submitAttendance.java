package com.sohamkore.attendancesuite;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class submitAttendance extends AppCompatActivity {

    private EditText attendancePinEditText;
    private Button submitButton;
    private String currentUserId;
    private FirebaseDatabase database;
    private DatabaseReference attendanceRef, pinRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_attendance);

        uiSetup();

        // Initialize Firebase
        database = FirebaseDatabase.getInstance();
        attendanceRef = database.getReference("attendance_records");
        pinRef = database.getReference("AttendancePins/currentPin");

        // Retrieve student details from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("AttendanceSuite", MODE_PRIVATE);
        currentUserId = sharedPreferences.getString("studentId", "id_not_found_contact_admin");

        // Get current date
        String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        // Initialize UI components
        attendancePinEditText = findViewById(R.id.attendancePinEditText);
        submitButton = findViewById(R.id.submitButton);

        // On Submit Button Click
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredPin = attendancePinEditText.getText().toString().trim();
                if (enteredPin.isEmpty()) {
                    Toast.makeText(submitAttendance.this, "Please enter the attendance pin.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Validate the pin
                pinRef.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DataSnapshot snapshot = task.getResult();
                        String currentPin = snapshot.getValue(String.class);

                        if (enteredPin.equals(currentPin)) {
                            // Check if attendance has already been submitted for the day
                            attendanceRef.child(currentUserId).child(currentDate).get().addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    if (task1.getResult().exists()) {
                                        Toast.makeText(submitAttendance.this, "Attendance already submitted for today.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        // Submit attendance
                                        Map<String, Object> attendanceData = new HashMap<>();
                                        attendanceData.put(currentDate, true);

                                        attendanceRef.child(currentUserId).updateChildren(attendanceData)
                                                .addOnCompleteListener(submitTask -> {
                                                    if (submitTask.isSuccessful()) {
                                                        Toast.makeText(submitAttendance.this, "Attendance submitted successfully.", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(submitAttendance.this, "Failed to submit attendance.", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                } else {
                                    Toast.makeText(submitAttendance.this, "Failed to check attendance submission status.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(submitAttendance.this, "Invalid attendance pin.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(submitAttendance.this, "Failed to retrieve attendance pin.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void uiSetup() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }
}
