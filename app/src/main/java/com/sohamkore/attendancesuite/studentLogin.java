package com.sohamkore.attendancesuite;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class studentLogin extends AppCompatActivity {

    private EditText etLoginEmail, etLoginPassword;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_login);

        // Check if user is already logged in
        SharedPreferences sharedPreferences = getSharedPreferences("AttendanceSuite", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        if (isLoggedIn) {
            // Redirect to the dashboard
            Intent intent = new Intent(studentLogin.this, StudentDashboard.class);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_student_login);
        uiSetup();

        // Initialize views
        etLoginEmail = findViewById(R.id.etLoginEmail);
        etLoginPassword = findViewById(R.id.etLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);

        // Set up Firebase reference
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference("students");

        // Set up Login button click listener
        btnLogin.setOnClickListener(v -> {
            String email = etLoginEmail.getText().toString();
            String password = etLoginPassword.getText().toString();

            // Validate input fields
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(studentLogin.this, "Please fill in both fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Query the Firebase database for the entered email
            databaseReference.orderByChild("email").equalTo(email)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    // Retrieve user details from Firebase
                                    String storedPassword = snapshot.child("password").getValue(String.class);
                                    if (storedPassword != null && storedPassword.equals(password)) {
                                        // Fetch all user info
                                        String name = snapshot.child("name").getValue(String.class);
                                        String email = snapshot.child("email").getValue(String.class);
                                        String contact = snapshot.child("contact").getValue(String.class);
                                        String dob = snapshot.child("dob").getValue(String.class);
                                        String rollNo = snapshot.child("rollNo").getValue(String.class);
                                        String department = snapshot.child("department").getValue(String.class);
                                        String studentClass = snapshot.child("studentClass").getValue(String.class);
                                        String studentId = snapshot.child("studentId").getValue(String.class);
                                        String address = snapshot.child("address").getValue(String.class);

                                        // Save user info locally in SharedPreferences
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putBoolean("isLoggedIn", true);
                                        editor.putString("studentEmail", email);
                                        editor.putString("studentName", name);
                                        editor.putString("studentId", studentId);
                                        editor.putString("studentRollNo", rollNo);
                                        editor.putString("studentDepartment", department);
                                        editor.putString("studentClass", studentClass);
                                        editor.putString("studentPass", password);
                                        editor.putString("studentContact", contact);
                                        editor.putString("studentDob", dob);
                                        editor.putString("studentAddress", address);
                                        editor.apply();

                                        // Login successful
                                        Toast.makeText(studentLogin.this, "Login Successful", Toast.LENGTH_SHORT).show();

                                        // Redirect to the dashboard
                                        Intent intent = new Intent(studentLogin.this, StudentDashboard.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        // Incorrect password
                                        Toast.makeText(studentLogin.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            } else {
                                // Email doesn't exist
                                Toast.makeText(studentLogin.this, "No account found with this email", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(studentLogin.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    private void uiSetup() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }
}
