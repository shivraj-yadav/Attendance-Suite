package com.sohamkore.attendancesuite;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

public class StudentOnboard extends AppCompatActivity {

    private EditText etStudentName, etRollNo, etDepartment, etEmail, etPassword, etContact, etAddress;
    private Spinner spinnerClass;
    private Button btnRegister, etDOB;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_onboard);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }


        // Check if user is already logged in
        SharedPreferences sharedPreferences = getSharedPreferences("AttendanceSuite", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        if (isLoggedIn) {
            // Redirect to the dashboard
            Intent intent = new Intent(StudentOnboard.this, StudentDashboard.class);
            startActivity(intent);
            finish();
            return;
        }

        TextView loginBtn = findViewById(R.id.login);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(StudentOnboard.this, studentLogin.class);
                startActivity(i);
            }
        });
        Button debug = findViewById(R.id.registerBtn);
        debug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(StudentOnboard.this, StudentDashboard.class);
                startActivity(i);
            }
        });

        // Initialize Firebase Database reference
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference("students");

        // Initialize views
        etStudentName = findViewById(R.id.etStudentName);
        etRollNo = findViewById(R.id.etRollNo);
        etDepartment = findViewById(R.id.etDepartment);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etContact = findViewById(R.id.etContact);
        etDOB = findViewById(R.id.etDOB);
        etAddress = findViewById(R.id.etAddress);
        spinnerClass = findViewById(R.id.spinnerClass);
        btnRegister = findViewById(R.id.btnRegister);

        // Set up Spinner (Class Dropdown)
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.class_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerClass.setAdapter(adapter);

        // Set up DatePicker for DOB
        calendar = Calendar.getInstance();
        etDOB.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    StudentOnboard.this, (view, year, monthOfYear, dayOfMonth) -> {
                calendar.set(year, monthOfYear, dayOfMonth);
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                etDOB.setText(sdf.format(calendar.getTime()));
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        // Register button click listener
        btnRegister.setOnClickListener(v -> {
            // Collect data
            String name = etStudentName.getText().toString();
            String rollNo = etRollNo.getText().toString();
            String department = etDepartment.getText().toString();
            String studentClass = spinnerClass.getSelectedItem().toString();
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();
            String contact = etContact.getText().toString();
            String dob = etDOB.getText().toString();
            String address = etAddress.getText().toString();

            // Validation
            if (name.isEmpty() || rollNo.isEmpty() || department.isEmpty() || email.isEmpty() ||
                    password.isEmpty() || contact.isEmpty() || dob.isEmpty() || address.isEmpty()) {
                Toast.makeText(StudentOnboard.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if student already exists
            databaseReference.orderByChild("email").equalTo(email)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                // User already exists
                                Toast.makeText(StudentOnboard.this, "Email already registered. Please log in.", Toast.LENGTH_SHORT).show();
                            } else {
                                // New user registration
                                String studentId = generateUniqueStudentId();  // Generate unique 4-digit ID
                                Student student = new Student(studentId, name, rollNo, department, studentClass, email, password, contact, dob, address);

                                databaseReference.child(studentId).setValue(student)
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(StudentOnboard.this, "Student Registered Successfully!", Toast.LENGTH_SHORT).show();
                                                // Save login info locally using SharedPreferences
                                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                                editor.putBoolean("isLoggedIn", true);
                                                editor.putString("studentEmail", email);
                                                editor.putString("studentName", name);
                                                editor.putString("studentId", studentId);
                                                editor.putString("studentRollNo", rollNo);
                                                editor.putString("studentDepartment", department);
                                                editor.putString("studentClass",studentClass);
                                                editor.putString("studentPass",password);
                                                editor.putString("studentContact",contact);
                                                editor.putString("studentDob",dob);
                                                editor.putString("studentAddress",address);
                                                editor.apply();
                                                Intent i = new Intent(StudentOnboard.this, StudentDashboard.class);
                                                startActivity(i);
                                                finish();
                                            } else {
                                                Toast.makeText(StudentOnboard.this, "Registration Failed. Try again.", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(StudentOnboard.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    // Method to generate a unique 4-digit student ID
    private String generateUniqueStudentId() {
        Random random = new Random();
        return String.format("%04d", random.nextInt(10000));  // Generate a 4-digit ID
    }

    // Define Student model class
    public static class Student {
        public String studentId, name, rollNo, department, studentClass, email, password, contact, dob, address;

        public Student(String studentId, String name, String rollNo, String department, String studentClass, String email, String password, String contact, String dob, String address) {
            this.studentId = studentId;
            this.name = name;
            this.rollNo = rollNo;
            this.department = department;
            this.studentClass = studentClass;
            this.email = email;
            this.password = password;
            this.contact = contact;
            this.dob = dob;
            this.address = address;
        }
    }

    private void redirectToDashboard() {
        Intent intent = new Intent(StudentOnboard.this,StudentDashboard.class);
        startActivity(intent);
        finish(); // Close the Registration Activity
    }
}
