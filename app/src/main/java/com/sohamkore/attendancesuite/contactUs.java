package com.sohamkore.attendancesuite;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class contactUs extends AppCompatActivity {

    private ImageView instaIcon, fbIcon, webIcon, callIcon, emailIcon;
    private EditText emailField, feedbackField;
    private Button submitFeedback;
    private DatabaseReference databaseFeedback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        uiSetup();
        drawerSetup();

        // Initialize Firebase Database reference
        databaseFeedback = FirebaseDatabase.getInstance().getReference("feedback");

        // Initialize Views
        instaIcon = findViewById(R.id.instaIcon);
        fbIcon = findViewById(R.id.fbIcon);
        webIcon = findViewById(R.id.webIcon);
        callIcon = findViewById(R.id.callIcon);
        emailIcon = findViewById(R.id.emailIcon);
        emailField = findViewById(R.id.emailField);
        feedbackField = findViewById(R.id.feedbackField);
        submitFeedback = findViewById(R.id.submitFeedback);

        // Set up social media icon actions
        instaIcon.setOnClickListener(v -> openLink("https://sohamkore.github.io/c"));
        fbIcon.setOnClickListener(v -> openLink("https://sohamkore.github.io"));
        webIcon.setOnClickListener(v -> openLink("https://sohamkore.github.io/Projects"));

        // Set up communication actions (call, email)
        callIcon.setOnClickListener(v -> initiateCall());
        emailIcon.setOnClickListener(v -> sendEmail());

        // Set up feedback submission
        submitFeedback.setOnClickListener(v -> submitFeedback());
    }

    private void drawerSetup() {
        ConstraintLayout drawer = findViewById(R.id.drawer);
        drawer.setVisibility(View.GONE);
        ImageView menuBtn = findViewById(R.id.menuBtn);
        menuBtn.setOnClickListener(v -> drawer.setVisibility(View.VISIBLE));

        ImageView drawerClose = findViewById(R.id.closeDrawer);
        drawerClose.setOnClickListener(v -> drawer.setVisibility(View.GONE));

        TextView homeDrawerBtn = findViewById(R.id.drawerHome);
        homeDrawerBtn.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(), Landing.class);
            startActivity(i);
        });

        TextView drawerContactBtn = findViewById(R.id.drawerContact);
        drawerContactBtn.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(), contactUs.class);
            startActivity(i);
        });

        TextView drawerAboutBtn = findViewById(R.id.drawerAbout);
        drawerAboutBtn.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(), aboutUs.class);
            startActivity(i);
        });
    }

    private void openLink(String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }

    private void initiateCall() {
        String phoneNumber = "+1234567890"; // Use dynamic phone number as needed
        Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
        startActivity(callIntent);
    }

    private void sendEmail() {
        String email = "sxhamk@gmail.com"; // Change to your support email
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + email));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback from App User");
        startActivity(Intent.createChooser(emailIntent, "Send Email"));
    }

    private void submitFeedback() {
        String userEmail = emailField.getText().toString().trim();
        String userMessage = feedbackField.getText().toString().trim();

        if (userEmail.isEmpty() || userMessage.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String feedbackId = databaseFeedback.push().getKey();
        Feedback userFeedback = new Feedback(userEmail, userMessage);

        if (feedbackId != null) {
            databaseFeedback.child(feedbackId).setValue(userFeedback)
                    .addOnSuccessListener(aVoid -> Toast.makeText(contactUs.this, "Feedback submitted!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(contactUs.this, "Failed to submit feedback", Toast.LENGTH_SHORT).show());
        }
    }

    private void uiSetup() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    public static class Feedback {
        private String email;
        private String message;

        // Default constructor (Required for Firebase)
        public Feedback() {
        }

        // Parameterized constructor
        public Feedback(String email, String message) {
            this.email = email;
            this.message = message;
        }

        // Getters and Setters (Required for Firebase)
        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
