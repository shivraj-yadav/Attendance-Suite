package com.sohamkore.attendancesuite;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatBoatActivity extends AppCompatActivity {

    private EditText userInput;
    private FloatingActionButton askBtn;
    private ProgressBar loadingIndicator;
    private LinearLayout messagesContainer;

    private final String API_KEY = "AIzaSyAHoVOgM3zvB0_89yGi7sAaUflb8CUkZcE"; // Replace with your actual API key
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_boat);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Initialize views
        userInput = findViewById(R.id.userInput);
        askBtn = findViewById(R.id.askBtn);
        loadingIndicator = findViewById(R.id.loadingIndicator);
        messagesContainer = findViewById(R.id.messagesContainer);

        // Set up click listener for the send button
        askBtn.setOnClickListener(view -> {
            String question = userInput.getText().toString().trim();
            if (!question.isEmpty()) {
                // Show loading indicator
                loadingIndicator.setVisibility(View.VISIBLE);

                // Add user message to the chat
                addUserMessageToChat(question);

                // Clear input field
                userInput.setText("");

                // Get AI response in background thread
                executorService.execute(() -> {
                    String answer = getAIResponse(question);

                    // Update UI on main thread
                    mainHandler.post(() -> {
                        loadingIndicator.setVisibility(View.GONE);
                        addAIResponseToChat(answer);
                    });
                });
            }
        });
    }

    // Method to add user message to chat UI
    private void addUserMessageToChat(String message) {
        View userMessageView = LayoutInflater.from(this).inflate(R.layout.user_message_bubble, null);
        TextView messageText = userMessageView.findViewById(R.id.messageText);
        messageText.setText(message);

        // Add to container
        messagesContainer.addView(userMessageView);

        // Scroll to bottom
        scrollToBottom();
    }

    // Method to add AI response to chat UI
    private void addAIResponseToChat(String message) {
        View aiMessageView = LayoutInflater.from(this).inflate(R.layout.ai_message_bubble, null);
        TextView messageText = aiMessageView.findViewById(R.id.messageText);
        messageText.setText(message);

        // Set up copy button
        ImageButton copyBtn = aiMessageView.findViewById(R.id.copyBtn);
        copyBtn.setOnClickListener(v -> {
            String textToCopy = messageText.getText().toString().trim();
            if (!textToCopy.isEmpty()) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("AI Response", textToCopy);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(ChatBoatActivity.this, "Response copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        });

        // Add to container
        messagesContainer.addView(aiMessageView);

        // Scroll to bottom
        scrollToBottom();
    }

    private void scrollToBottom() {
        View scrollView = findViewById(R.id.scrollView);
        scrollView.post(() -> {
            ((androidx.core.widget.NestedScrollView) scrollView).fullScroll(View.FOCUS_DOWN);
        });
    }

    private String getAIResponse(String prompt) {
        try {
            URL url = new URL("https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-pro:generateContent?key=" + API_KEY);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            JSONObject content = new JSONObject();
            content.put("parts", new JSONArray().put(new JSONObject().put("text", prompt)));

            JSONObject body = new JSONObject();
            body.put("contents", new JSONArray().put(content));

            OutputStream os = conn.getOutputStream();
            os.write(body.toString().getBytes());
            os.flush();
            os.close();

            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) sb.append(line);

            JSONObject response = new JSONObject(sb.toString());
            String aiText = response.getJSONArray("candidates")
                    .getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text");

            return aiText;
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}