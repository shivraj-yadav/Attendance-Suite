package com.sohamkore.attendancesuite;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class attendanceReport extends AppCompatActivity {
    String studentId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_report);
        uiSetup();

        WebView webView = findViewById(R.id.webView);

        // Enable JavaScript and other WebView settings
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);

        // Add the interface for JavaScript to interact with Android
        webView.addJavascriptInterface(new WebAppInterface(), "Android");

        // Load your local HTML file
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("file:///android_asset/attendanceReport.html");

        // Initialize SharedPreferences
        SharedPreferences sharedPreferences;
        sharedPreferences = getSharedPreferences("AttendanceSuite", MODE_PRIVATE);
        studentId = sharedPreferences.getString("studentId", "id_not_found_contact_Admin");
        String studentName = sharedPreferences.getString("studentName", "name_not_found_contact_Admin");
        Toast.makeText(this, "Student ID is - "+studentId, Toast.LENGTH_SHORT).show();

    }

    private void uiSetup() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    // Define the interface for communication
    public class WebAppInterface {
        @JavascriptInterface
        public String getStudentId() {
            // Replace this with logic to fetch the actual student ID, e.g., from SharedPreferences
            return studentId;
        }
    }
}
