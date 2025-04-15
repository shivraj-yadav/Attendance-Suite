package com.sohamkore.attendancesuite;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class studentParent extends AppCompatActivity {
    private TextView parentName, parentEmail, parentContact;
    private EditText inputParentId;
    private Button btnLinkParent, btnUnlinkParent;

    private DatabaseReference parentsRef, studentsRef;
    private String studentId = "student_id_1"; // Replace with the logged-in student's ID.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_parent);

        // Initialize Firebase references
        parentsRef = FirebaseDatabase.getInstance().getReference("parents");
        studentsRef = FirebaseDatabase.getInstance().getReference("students");

        // Initialize UI elements
        parentName = findViewById(R.id.parent_name);
        parentEmail = findViewById(R.id.parent_email);
        parentContact = findViewById(R.id.parent_contact);
        inputParentId = findViewById(R.id.input_parent_id);
        btnLinkParent = findViewById(R.id.btn_link_parent);
        btnUnlinkParent = findViewById(R.id.btn_unlink_parent);

        // Check if parent is linked
        loadParentDetails();

        // Link Parent Button
        btnLinkParent.setOnClickListener(view -> linkParent());

        // Unlink Parent Button
        btnUnlinkParent.setOnClickListener(view -> unlinkParent());
    }

    private void loadParentDetails() {
        // Load parent details logic (use Firebase to fetch linked parent details for studentId)
        // If linked, show parent details, else show the linking form
    }

    private void linkParent() {
        String parentId = inputParentId.getText().toString().trim();
        if (parentId.isEmpty()) {
            Toast.makeText(this, "Parent ID cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // Logic to link parentId with studentId in the Firebase database
        parentsRef.child(parentId).child("children").child(studentId).setValue(true)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Parent linked successfully!", Toast.LENGTH_SHORT).show();
                    loadParentDetails();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to link parent!", Toast.LENGTH_SHORT).show());
    }

    private void unlinkParent() {
        // Logic to unlink parentId from studentId in the Firebase database
    }
}