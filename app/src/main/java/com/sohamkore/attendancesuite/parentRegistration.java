package com.sohamkore.attendancesuite;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class parentRegistration extends AppCompatActivity {

    private EditText etParentName, etParentEmail, etParentPassword, etParentContact, etChildStudentId;
    private Button btnRegister;
    private TextView tvLoginLink;
    private DatabaseReference parentsRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize SharedPreferences
        SharedPreferences sharedPreferences;
        sharedPreferences = getSharedPreferences("ParentPrefs", MODE_PRIVATE);

        // Check if the parent is already logged in
        if (sharedPreferences.getBoolean("isLoggedIn", false)) {
            redirectToDashboard();
            return; // Skip registration screen
        }

        setContentView(R.layout.activity_parent_registration);
        uiSetup();

        // Initialize Views
        etParentName = findViewById(R.id.etParentName);
        etParentEmail = findViewById(R.id.etParentEmail);
        etParentPassword = findViewById(R.id.etParentPassword);
        etParentContact = findViewById(R.id.etParentContact);
        etChildStudentId = findViewById(R.id.etChildStudentId);
        btnRegister = findViewById(R.id.btnRegister);
        tvLoginLink = findViewById(R.id.tvLoginLink);

        // Firebase reference
        parentsRef = FirebaseDatabase.getInstance().getReference("parents");

        btnRegister.setOnClickListener(v -> {
            String name = etParentName.getText().toString().trim();
            String email = etParentEmail.getText().toString().trim();
            String password = etParentPassword.getText().toString().trim();
            String contact = etParentContact.getText().toString().trim();
            String studentId = etChildStudentId.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || contact.isEmpty() || studentId.isEmpty()) {
                Toast.makeText(parentRegistration.this, "Please fill all the fields.", Toast.LENGTH_SHORT).show();
            } else {
                // Save parent info to Firebase
                String parentId = parentsRef.push().getKey();
                Parent parent = new Parent(name, email, password, contact, studentId);
                parentsRef.child(parentId).setValue(parent);

                // Save login info locally using SharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isLoggedIn", true);
                editor.putString("parentEmail", email);
                editor.putString("studentId", studentId);
                editor.putString("parentName", name);
                editor.putString("parentContact", contact);
                editor.putString("parentPassword", password);

                editor.apply();

                Toast.makeText(parentRegistration.this, "Registration successful", Toast.LENGTH_SHORT).show();

                // Redirect to Dashboard
                redirectToDashboard();
            }
        });

        tvLoginLink.setOnClickListener(v -> {
            startActivity(new Intent(parentRegistration.this, parentLogin.class));
        });
    }

    // Define Parent model class
    public static class Parent {
        public String name, email, password, contact, studentId;

        public Parent(String name, String email, String password, String contact, String studentId) {
            this.name = name;
            this.email = email;
            this.password = password;
            this.contact = contact;
            this.studentId = studentId;
        }
    }

    private void uiSetup() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    private void redirectToDashboard() {
        Intent intent = new Intent(parentRegistration.this, parentDashboard.class);
        startActivity(intent);
        finish(); // Close the Registration Activity
    }
}
