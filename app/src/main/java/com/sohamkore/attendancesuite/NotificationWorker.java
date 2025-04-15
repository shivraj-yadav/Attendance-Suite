package com.sohamkore.attendancesuite;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashSet;
import java.util.Set;

public class NotificationWorker extends Worker {

    private static final String LAST_NOTIFICATION_IDS_KEY = "last_notification_ids";
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final DatabaseReference notificationsRef = database.getReference("notifications");

    public NotificationWorker(Context context, WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @Override
    public Result doWork() {
        // Fetch the set of notification IDs that have already been shown
        Set<String> shownNotificationIds = getShownNotificationIds();

        notificationsRef.orderByChild("notification_id").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String notificationId = snapshot.child("notification_id").getValue(String.class);
                        String from = snapshot.child("from").getValue(String.class);
                        String subject = snapshot.child("subject").getValue(String.class);
                        String message = snapshot.child("message").getValue(String.class);

                        // Only show the notification if it hasn't been shown already
                        if (!shownNotificationIds.contains(notificationId)) {
                            // Show notification
                            showNotification(from, subject, message);

                            // Add the new notification ID to the set
                            shownNotificationIds.add(notificationId);
                        }
                    }

                    // Save the updated list of shown notification IDs to SharedPreferences
                    saveShownNotificationIds(shownNotificationIds);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("NotificationWorker", "Error fetching notifications: " + databaseError.getMessage());
            }
        });

        return Result.success();
    }

    private void showNotification(String from, String subject, String message) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), "default")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(subject)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);


        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
    }

    private Set<String> getShownNotificationIds() {
        // Fetch the set of notification IDs from SharedPreferences
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("notifications", Context.MODE_PRIVATE);
        return prefs.getStringSet(LAST_NOTIFICATION_IDS_KEY, new HashSet<>());
    }

    private void saveShownNotificationIds(Set<String> notificationIds) {
        // Save the updated set of notification IDs to SharedPreferences
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("notifications", Context.MODE_PRIVATE);
        prefs.edit().putStringSet(LAST_NOTIFICATION_IDS_KEY, notificationIds).apply();
    }
}
