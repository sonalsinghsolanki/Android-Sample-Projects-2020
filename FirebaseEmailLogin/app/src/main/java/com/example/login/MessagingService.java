package com.example.login;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;


import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
       // super.onMessageReceived(remoteMessage);
        showNotifications(remoteMessage.getNotification().getBody());

    }

    public void showNotifications(String message){
        PendingIntent intent = PendingIntent.getActivity(this,0,
                new Intent(this,Account.class),0);
       Notification notification = new Notification.Builder(MessagingService.this)
               .setContentTitle("New Message")
               .setContentText(message)
               .setContentIntent(intent)
               .setAutoCancel(true)
               .setSmallIcon(R.drawable.ic_stat_ic_notification)
               .build();
       NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
       notificationManager.notify(0,notification);
    }
}
