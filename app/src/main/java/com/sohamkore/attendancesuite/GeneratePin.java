package com.sohamkore.attendancesuite;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class GeneratePin extends AppCompatActivity {
    private EditText etPinLength;
    private TextView tvGeneratedPin;
    private Button btnGeneratePin;
    private DatabaseReference databaseReference;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_pin);

        etPinLength = findViewById(R.id.et_pin_length);
        tvGeneratedPin = findViewById(R.id.tv_generated_pin);
        btnGeneratePin = findViewById(R.id.btn_generate_pin);

        databaseReference = FirebaseDatabase.getInstance().getReference("AttendancePins");

        checkPinExpiry();
        uiSetup();

        btnGeneratePin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String lengthStr = etPinLength.getText().toString();
                if (lengthStr.isEmpty()) {
                    Toast.makeText(GeneratePin.this, "Please enter pin length", Toast.LENGTH_SHORT).show();
                    return;
                }
                int pinLength = Integer.parseInt(lengthStr);
                if (pinLength <= 0) {
                    Toast.makeText(GeneratePin.this, "Pin length must be greater than 0", Toast.LENGTH_SHORT).show();
                    return;
                }
                String newPin = generatePin(pinLength);
                savePinAndExpiry(newPin);
                schedulePinExpiryWorker();
                generateAndSavePin(newPin);
            }
        });
    }

    private void generateAndSavePin(String pin) {
        databaseReference.child("currentPin").setValue(pin);
        tvGeneratedPin.setText("Generated Pin: " + pin);
        Toast.makeText(this, "New pin generated and saved", Toast.LENGTH_SHORT).show();
    }

    private void savePinAndExpiry(String pin) {
        SharedPreferences sharedPreferences = getSharedPreferences("PinPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        long expiryTime = System.currentTimeMillis() + (15 * 60 * 1000);
        editor.putString("generatedPin", pin);
        editor.putLong("expiryTime", expiryTime);
        editor.apply();
        startCountdownTimer(expiryTime);
    }

    private void startCountdownTimer(long expiryTime) {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        long timeRemaining = expiryTime - System.currentTimeMillis();
        countDownTimer = new CountDownTimer(timeRemaining, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long minutes = millisUntilFinished / (60 * 1000);
                long seconds = (millisUntilFinished % (60 * 1000)) / 1000;
                tvGeneratedPin.setText("Generated Pin: " + getSavedPin() + " (Expires in " + minutes + ":" + seconds + ")");
            }
            @Override
            public void onFinish() {
                tvGeneratedPin.setText("PIN Expired");
                savePinAndExpiry("Expired");
            }
        }.start();
    }

    private void schedulePinExpiryWorker() {
        OneTimeWorkRequest expiryWorkRequest = new OneTimeWorkRequest.Builder(PinExpiryWorker.class)
                .setInitialDelay(15, TimeUnit.MINUTES)
                .build();
        WorkManager.getInstance(this).enqueue(expiryWorkRequest);
    }

    private void checkPinExpiry() {
        SharedPreferences sharedPreferences = getSharedPreferences("PinPrefs", MODE_PRIVATE);
        String savedPin = sharedPreferences.getString("generatedPin", null);
        long expiryTime = sharedPreferences.getLong("expiryTime", 0);
        if (savedPin == null || expiryTime == 0) {
            tvGeneratedPin.setText("No pin currently generated.");
            return;
        }
        if (System.currentTimeMillis() >= expiryTime) {
            tvGeneratedPin.setText("PIN Expired");
        } else {
            tvGeneratedPin.setText("Generated Pin: " + savedPin);
            startCountdownTimer(expiryTime);
        }
    }

    private String getSavedPin() {
        SharedPreferences sharedPreferences = getSharedPreferences("PinPrefs", MODE_PRIVATE);
        return sharedPreferences.getString("generatedPin", "Expired");
    }

    private String generatePin(int length) {
        Random random = new Random();
        StringBuilder pin = new StringBuilder();
        for (int i = 0; i < length; i++) {
            pin.append(random.nextInt(10));
        }
        return pin.toString();
    }

    private void uiSetup() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    public static class PinExpiryWorker extends Worker {
        public PinExpiryWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
            super(context, workerParams);
        }
        @NonNull
        @Override
        public Result doWork() {
            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("PinPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("generatedPin", "Expired");
            editor.apply();
            return Result.success();
        }
    }
}
