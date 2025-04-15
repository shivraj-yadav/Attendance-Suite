package com.sohamkore.attendancesuite;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class teacherLogin extends AppCompatActivity {
    private EditText loginEmail, loginPassword;
    private Button btnLogin;
    private TextView registerLink;
    private DatabaseReference databaseReference;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_login);
uiSetup();
        loginEmail = findViewById(R.id.loginEmail);
        loginPassword = findViewById(R.id.loginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        registerLink = findViewById(R.id.registerBtn);

        databaseReference = FirebaseDatabase.getInstance().getReference("teachers");
        sharedPreferences = getSharedPreferences("TeacherPrefs", MODE_PRIVATE);

        // Login Button Click Listener
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = loginEmail.getText().toString().trim();
                String password = loginPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    Toast.makeText(teacherLogin.this, "Please enter all fields", Toast.LENGTH_SHORT).show();
                } else {
                    authenticateTeacher(email, password);
                }
            }
        });

        // Register Link Click Listener
        registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(teacherLogin.this, TeacherRegistration.class);
                startActivity(intent);
            }
        });
    }

    private void authenticateTeacher(String email, String password) {
        databaseReference.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String dbPassword = snapshot.child("password").getValue(String.class);
                        String fullName = snapshot.child("name").getValue(String.class);
                        if (dbPassword != null && dbPassword.equals(password)) {
                            // Fetch teacherId and teacherName
                            String teacherId = snapshot.getKey();

                            // Save teacherId and teacherName to SharedPreferences
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("teacherId", teacherId); // Save teacherId
                            editor.putString("teacherName", fullName); // Save teacherName
                            editor.apply(); // Commit changes

                            // Notify user of successful login
                            Toast.makeText(teacherLogin.this, "Login Successful", Toast.LENGTH_SHORT).show();

                            // Navigate to the teacher dashboard
                            Intent intent = new Intent(teacherLogin.this, teacherDashboard.class);
                            startActivity(intent);
                            finish(); // Close the login activity
                        } else {
                            Toast.makeText(teacherLogin.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(teacherLogin.this, "Teacher not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(teacherLogin.this, "Database Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
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
