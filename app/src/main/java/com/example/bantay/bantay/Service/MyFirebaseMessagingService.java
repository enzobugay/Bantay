package com.example.bantay.bantay.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.admin.DeviceAdminInfo;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.bantay.bantay.AcknowledgeNotif;
import com.example.bantay.bantay.AlertNotif;
import com.example.bantay.bantay.AlertNotif2;
import com.example.bantay.bantay.AlertNotif3;
import com.example.bantay.bantay.CctvFragment;
import com.example.bantay.bantay.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        showNotification(remoteMessage.getData());
       // if (remoteMessage.getData().isEmpty()) {
            //showNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
        /*} else {

        }*/
    }

    private void showNotification(Map<String, String> data) {
        String title = data.get("title");
        String body = data.get("body");

        Intent intent = new Intent(this, AlertNotif.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intent1 = new Intent(this, AlertNotif2.class);
        PendingIntent pendingIntent1 = PendingIntent.getActivity(this, 2, intent1, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intent2 = new Intent(this, AlertNotif3.class);
        PendingIntent pendingIntent2 = PendingIntent.getActivity(this, 3, intent2, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intent3 = new Intent(this, AcknowledgeNotif.class);
        PendingIntent pendingIntent3 = PendingIntent.getActivity(this, 4, intent3, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultsound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Uri alarmsound = Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/" + R.raw.siren); //ContentResolver.SCHEME_ANDROID_RESOURCE +

        String NOTIFICATION_CHANNEL_ID = "com.example.bantay.bantay";
        String NOTIFICATION_CHANNEL_ID2 = "com.example.bantay.bantay.ack";

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        //Notifcation builder for alert level notification
        if(title.toLowerCase().contains("level")) {
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);

            notificationBuilder.setAutoCancel(true);
            notificationBuilder.setPriority(Notification.PRIORITY_MAX);
            notificationBuilder.setDefaults(Notification.DEFAULT_ALL);
            notificationBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
            notificationBuilder.setDefaults(Notification.DEFAULT_SOUND);
            notificationBuilder.setWhen(System.currentTimeMillis());
            notificationBuilder.setSmallIcon(R.drawable.marikinalogo);
            notificationBuilder.setContentTitle(title);
            notificationBuilder.setContentText(body);
            notificationBuilder.setVibrate(new long[]{500, 1000, 500, 1000, 500, 1000});
            notificationBuilder.setSound(alarmsound);

            if (title.toLowerCase().contains("1")) {
                notificationBuilder.setContentIntent(pendingIntent);
                notificationBuilder.setSound(alarmsound);
            } else if (title.toLowerCase().contains("2")) {
                notificationBuilder.setContentIntent(pendingIntent1);
                notificationBuilder.setSound(alarmsound);
            } else if (title.toLowerCase().contains("3")) {
                notificationBuilder.setContentIntent(pendingIntent2);
                notificationBuilder.setSound(alarmsound);
            }/* else {
                notificationBuilder.setContentIntent(pendingIntent3);
                notificationBuilder.setSound(defaultsound);
            }*/

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Notification", NotificationManager.IMPORTANCE_HIGH);

                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .build();

                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.RED);
                notificationChannel.enableVibration(true);
                notificationChannel.setSound(alarmsound, audioAttributes);
                notificationChannel.setVibrationPattern(new long[]{500, 1000, 500, 1000, 500, 1000});
                notificationManager.createNotificationChannel(notificationChannel);
            }

            notificationManager.notify(0, notificationBuilder.build()); //new Random().nextInt()
        }
        //Notification builder for Acknowledge notification
        else{
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID2);

            notificationBuilder.setAutoCancel(true);
            notificationBuilder.setPriority(Notification.PRIORITY_MAX);
            notificationBuilder.setDefaults(Notification.DEFAULT_ALL);
            notificationBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
            notificationBuilder.setDefaults(Notification.DEFAULT_SOUND);
            notificationBuilder.setWhen(System.currentTimeMillis());
            notificationBuilder.setSmallIcon(R.drawable.marikinalogo);
            notificationBuilder.setContentTitle(title);
            notificationBuilder.setContentText(body);
            notificationBuilder.setVibrate(new long[]{500, 1000, 500, 1000, 500, 1000});

                notificationBuilder.setContentIntent(pendingIntent3);
                notificationBuilder.setSound(defaultsound);


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID2, "Notification", NotificationManager.IMPORTANCE_HIGH);

                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .build();

                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.RED);
                notificationChannel.enableVibration(true);
                notificationChannel.setSound(defaultsound, audioAttributes);
                notificationChannel.setVibrationPattern(new long[]{500, 1000, 500, 1000, 500, 1000});
                notificationManager.createNotificationChannel(notificationChannel);
            }

            notificationManager.notify(0, notificationBuilder.build()); //new Random().nextInt()
        }
    }


   /* private void showNotification(String title, String body) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "com.example.bantay.bantay.test";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Notification", NotificationManager.IMPORTANCE_DEFAULT);


            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);

        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.marikinalogo)
                .setContentTitle(title)
                .setContentText(body);


        notificationManager.notify(new Random().nextInt(), notificationBuilder.build());
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        String deviceToken = s;
        Log.d("TokenDebug", deviceToken);


    }*/
}