package com.sohamkore.attendancesuite;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StudentDetailsActivity extends AppCompatActivity {
    // JavaScript Interface to expose studentId to JavaScript
    public static class WebAppInterface {
        private String studentId;

        WebAppInterface(String studentId) {
            this.studentId = studentId;
        }

        @JavascriptInterface
        public String getStudentId() {
            Log.d("WebAppInterface", "getStudentId called, returning: " + studentId);
            return studentId;
        }

    }

    private String studentId;
    private TextView tvName, tvId, tvContact, tvEmail, tvClass, tvDepartment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_details);

        uiSetup();
        initViews();
        Intent intent = getIntent();
        studentId = intent.getStringExtra("studentId");
        Log.d("StudentDetailsActivity", "Received Student ID: " + studentId);
        // Setup WebView
        WebView webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());

        // Pass studentId to WebAppInterface
        webView.addJavascriptInterface(new WebAppInterface(studentId), "Android");
        webView.loadUrl("file:///android_asset/attendanceReport.html");

        studentId = getIntent().getStringExtra("studentId");
        Log.d("StudentDetailsActivity", "Received Student ID: " + studentId);

        if (studentId != null) {
            fetchStudentDetails(studentId);
        } else {
            Toast.makeText(this, "Student ID not provided!", Toast.LENGTH_SHORT).show();
        }
    }

    private void initViews() {
        tvName = findViewById(R.id.tvName);
        tvId = findViewById(R.id.tvId);
        tvContact = findViewById(R.id.tvContact);
        tvEmail = findViewById(R.id.tvEmail);
        tvClass = findViewById(R.id.tvClass);
        tvDepartment = findViewById(R.id.tvDepartment);
    }

    private void uiSetup() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    private void fetchStudentDetails(String studentId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("students").child(studentId);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String contact = snapshot.child("contact").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    String studentClass = snapshot.child("studentClass").getValue(String.class);
                    String department = snapshot.child("department").getValue(String.class);

                    // Update UI with fetched data
                    tvName.setText("Name: " + name);
                    tvId.setText("ID: " + studentId);
                    tvContact.setText("Contact: " + contact);
                    tvEmail.setText("Email: " + email);
                    tvClass.setText("Class: " + studentClass);
                    tvDepartment.setText("Department: " + department);
                } else {
                    Toast.makeText(StudentDetailsActivity.this, "Student not found!", Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("StudentDetailsActivity", "Database error: " + error.getMessage());
                Toast.makeText(StudentDetailsActivity.this, "Failed to load student details.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

