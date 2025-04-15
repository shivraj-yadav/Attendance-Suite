package com.sohamkore.attendancesuite;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class teacher_absent_students extends AppCompatActivity {

    private static final int SMS_PERMISSION_REQUEST_CODE = 1; // Unique request code for SMS permission
    private static final String CORRECT_PIN = "3555"; // Predefined PIN

    private RecyclerView absentStudentsRecyclerView;
    private EditText smsMessageEditText;
    private Button sendSmsButton;
    private List<Student> absentStudentsList;
    private AbsentStudentsAdapter absentStudentsAdapter;

    private DatabaseReference studentsRef;
    private DatabaseReference attendanceRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_absent_students);
        uiSetup();

        // Initialize views
        absentStudentsRecyclerView = findViewById(R.id.absentStudentsRecyclerView);
        smsMessageEditText = findViewById(R.id.smsMessageEditText);
        sendSmsButton = findViewById(R.id.sendSmsButton);

        // Initialize Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        studentsRef = database.getReference("students");
        attendanceRef = database.getReference("attendance_records");

        // Set up RecyclerView
        absentStudentsList = new ArrayList<>();
        absentStudentsAdapter = new AbsentStudentsAdapter(absentStudentsList);
        absentStudentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        absentStudentsRecyclerView.setAdapter(absentStudentsAdapter);

        // Fetch data and identify absent students
        fetchDataAndIdentifyAbsentStudents();

        // Send SMS button click listener
        sendSmsButton.setOnClickListener(v -> {
            if (checkSmsPermission()) {
                showPinInputDialog(); // Show PIN input dialog
            } else {
                requestSmsPermission();
            }
        });
    }

    // Show PIN input dialog
    private void showPinInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter PIN");

        // Inflate the dialog layout
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_pin_input, null);
        final EditText pinInput = view.findViewById(R.id.pinInput);
        builder.setView(view);

        // Set up the buttons
        builder.setPositiveButton("Submit", (dialog, which) -> {
            String enteredPin = pinInput.getText().toString().trim();
            if (enteredPin.equals(CORRECT_PIN)) {
                sendSmsToAbsentStudents(); // Send SMS if PIN is correct
            } else {
                Toast.makeText(this, "Incorrect PIN. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    // Check if the SEND_SMS permission is granted
    private boolean checkSmsPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED;
    }

    // Request the SEND_SMS permission
    private void requestSmsPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_REQUEST_CODE);
    }

    // Handle the permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SMS_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, show PIN input dialog
                showPinInputDialog();
            } else {
                // Permission denied, show a message to the user
                Toast.makeText(this, "SMS permission is required to send messages.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void fetchDataAndIdentifyAbsentStudents() {
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        studentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot studentsSnapshot) {
                attendanceRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot attendanceSnapshot) {
                        Set<String> presentStudents = new HashSet<>();

                        // Identify present students
                        for (DataSnapshot studentSnapshot : attendanceSnapshot.getChildren()) {
                            if (studentSnapshot.child(today).exists()) {
                                presentStudents.add(studentSnapshot.getKey());
                            }
                        }

                        // Identify absent students
                        for (DataSnapshot studentSnapshot : studentsSnapshot.getChildren()) {
                            String studentId = studentSnapshot.getKey();
                            if (!presentStudents.contains(studentId)) {
                                Student student = studentSnapshot.getValue(Student.class);
                                absentStudentsList.add(student);
                            }
                        }

                        // Update RecyclerView
                        absentStudentsAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(teacher_absent_students.this, "Failed to fetch attendance data.", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(teacher_absent_students.this, "Failed to fetch students data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendSmsToAbsentStudents() {
        String smsMessage = smsMessageEditText.getText().toString().trim();

        if (smsMessage.isEmpty()) {
            Toast.makeText(this, "Please enter a message.", Toast.LENGTH_SHORT).show();
            return;
        }

        SmsManager smsManager = SmsManager.getDefault();
        for (Student student : absentStudentsList) {
            String phoneNumber = student.getContact();
            if (phoneNumber != null && !phoneNumber.isEmpty()) {
                smsManager.sendTextMessage(phoneNumber, null, smsMessage, null, null);
            }
        }

        Toast.makeText(this, "SMS sent to absent students.", Toast.LENGTH_SHORT).show();
    }

    private void uiSetup() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }
}