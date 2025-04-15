package com.sohamkore.attendancesuite;// Java File (src/main/java/com/example/TeacherRegistrationActivity.java)

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TeacherRegistration extends AppCompatActivity {


    private EditText etName, etEmail, etPassword;
    private Button btnRegister;
    TextView alreadyRegistered;
    private DatabaseReference teachersRef;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_registration);
        uiSetup();

        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnRegister = findViewById(R.id.btn_register);

        teachersRef = FirebaseDatabase.getInstance().getReference("teachers");
        sharedPreferences = getSharedPreferences("TeacherPrefs", MODE_PRIVATE);
        alreadyRegistered = findViewById(R.id.alreadyRegisteredBtn);
        alreadyRegistered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TeacherRegistration.this, teacherLogin.class));
            }
        });
        checkIfLoggedIn();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerTeacher();
            }
        });
    }
    void checkIfLoggedIn() {
        String teacherId = sharedPreferences.getString("teacherId", null);
        if (teacherId != null) {
            Toast.makeText(this, "Already logged in", Toast.LENGTH_SHORT).show();
            // Redirect to Dashboard
            startActivity(new Intent(TeacherRegistration.this, teacherDashboard.class));
            finish();
        }
    }

        private void registerTeacher() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        teachersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Toast.makeText(TeacherRegistration.this, "Email already registered", Toast.LENGTH_SHORT).show();
                } else {
                    String teacherId = teachersRef.push().getKey();
                    teachersRef.child(teacherId).setValue(new Teacher(name, email, password));
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("teacherId", teacherId);
                    editor.putString("teacherName", name);
                    editor.apply();
                    Toast.makeText(TeacherRegistration.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                    // Redirect to Dashboard
                    startActivity(new Intent(TeacherRegistration.this, teacherDashboard.class));
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error ){
                Toast.makeText(TeacherRegistration.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static class Teacher {
        public String name, email, password;

        public Teacher(String name, String email, String password) {
            this.name = name;
            this.email = email;
            this.password = password;
        }
    }
    private void uiSetup() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }
}
