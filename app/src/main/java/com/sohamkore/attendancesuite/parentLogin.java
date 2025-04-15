package com.sohamkore.attendancesuite;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class parentLogin extends AppCompatActivity {
    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin;
    private SharedPreferences sharedPreferences;
    private FirebaseDatabase database;
    private DatabaseReference parentsRef, studentsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiSetup();

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("ParentPrefs", MODE_PRIVATE);

        // Check if the parent is already logged in
        if (sharedPreferences.getBoolean("isLoggedIn", false)) {
            redirectToDashboard();
            return; // Skip the login screen if already logged in
        }

        setContentView(R.layout.activity_parent_login);

        // Initialize Firebase
        database = FirebaseDatabase.getInstance();
        parentsRef = database.getReference("parents");
        studentsRef = database.getReference("students");

        // Initialize views
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);

        // Login button click event
        buttonLogin.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                editTextEmail.setError("Email is required!");
                return;
            }
            if (TextUtils.isEmpty(password)) {
                editTextPassword.setError("Password is required!");
                return;
            }

            // Authenticate with Firebase
            authenticateParent(email, password);
        });
    }
String parentName, parentContact;
    private void authenticateParent(String email, String password) {
        parentsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                boolean found = false;
                for (DataSnapshot snapshot : task.getResult().getChildren()) {
                    String dbEmail = snapshot.child("email").getValue(String.class);
                    String dbPassword = snapshot.child("password").getValue(String.class);
                    parentName =  snapshot.child("name").getValue(String.class);
                    parentContact =  snapshot.child("contact").getValue(String.class);
                    if (dbEmail != null && dbPassword != null && dbEmail.equals(email) && dbPassword.equals(password)) {
                        found = true;

                        // Fetch parent and student details
                        String parentId = snapshot.getKey();
                        String studentId = snapshot.child("studentId").getValue(String.class);

                        if (studentId != null) {
                            // Fetch student details using the studentId
                            fetchStudentDetails(studentId, parentId);
                        } else {
                            Toast.makeText(parentLogin.this, "No student linked to this parent", Toast.LENGTH_SHORT).show();
                        }

                        break;
                    }
                }

                if (!found) {
                    Toast.makeText(parentLogin.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(parentLogin.this, "Failed to connect to the database.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchStudentDetails(String studentId, String parentId) {
        studentsRef.child(studentId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DataSnapshot studentSnapshot = task.getResult();

                String studentName = studentSnapshot.child("name").getValue(String.class);
                String studentEmail = studentSnapshot.child("email").getValue(String.class);
                String studentContact = studentSnapshot.child("contact").getValue(String.class);
                String studentDob = studentSnapshot.child("dob").getValue(String.class);
                String studentRollNo = studentSnapshot.child("rollNo").getValue(String.class);
                String studentDepartment = studentSnapshot.child("department").getValue(String.class);
                String studentClass = studentSnapshot.child("studentClass").getValue(String.class);
                String studentAddress = studentSnapshot.child("address").getValue(String.class);

                // Save parent and student details in SharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isLoggedIn", true); // Mark as logged in
                editor.putString("parentEmail", editTextEmail.getText().toString()); // Save parent's email
                editor.putString("parentName", parentName); // Save parent's email
                editor.putString("parentContact", parentContact); // Save parent's email
                editor.putString("parentId", parentId); // Save parent ID for future use
                editor.putString("studentId", studentId); // Save linked student ID
                editor.putString("studentName", studentName);
                editor.putString("studentEmail", studentEmail);
                editor.putString("studentContact", studentContact);
                editor.putString("studentDob", studentDob);
                editor.putString("studentRollNo", studentRollNo);
                editor.putString("studentDepartment", studentDepartment);
                editor.putString("studentClass", studentClass);
                editor.putString("studentAddress", studentAddress);
                editor.apply(); // Apply changes to SharedPreferences

                // Redirect to Dashboard
                Toast.makeText(parentLogin.this, "Login Successful", Toast.LENGTH_SHORT).show();
                redirectToDashboard();
            } else {
                Toast.makeText(parentLogin.this, "Failed to fetch student details.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void redirectToDashboard() {
        Intent intent = new Intent(parentLogin.this, parentDashboard.class);
        startActivity(intent);
        finish(); // Close the LoginActivity
    }

    private void uiSetup() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }
}
