package com.gtxprime.vtroid.Services;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.gtxprime.vtroid.Activities.Home;
import com.gtxprime.vtroid.Activities.WebActivity;
import com.gtxprime.vtroid.R;

@SuppressLint({"MissingFirebaseInstanceTokenRefresh"})
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    String channelId = "fcm_default_channel";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        System.out.println("From: " + remoteMessage.getFrom());
        if (remoteMessage.getData().size() > 0) {
            sendNotificationX(remoteMessage);
        }
    }

    private void sendNotificationX(RemoteMessage remoteMessage) {
        Intent intent;
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");
        String imgUrl = remoteMessage.getData().get("imgUrl");
        String work = remoteMessage.getData().get("work");
        String link = remoteMessage.getData().get("link");

        if (work != null && work.equals("update")) {
            intent = new Intent(this, Home.class);
            intent.putExtra("shouldCheck", true);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        } else {
            intent = new Intent(this, WebActivity.class);
            intent.putExtra("links", link);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }

        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent, PendingIntent.FLAG_UPDATE_CURRENT & PendingIntent.FLAG_IMMUTABLE);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        String channelName = "New Updates n Uploads";
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        try {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(channelId,
                        channelName,
                        NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(channel);
            }
            NotificationCompat.BigPictureStyle style;

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                style = new NotificationCompat.BigPictureStyle()
                        .setBigContentTitle(title)
                        .showBigPictureWhenCollapsed(true)
                        .bigLargeIcon(Glide.with(MyFirebaseMessagingService.this).asBitmap().load(imgUrl).submit().get())
                        .bigPicture(Glide.with(MyFirebaseMessagingService.this).asBitmap().load(imgUrl).submit().get());
            } else {
                style = new NotificationCompat.BigPictureStyle()
                        .setBigContentTitle(title)
                        .bigPicture(Glide.with(MyFirebaseMessagingService.this).asBitmap().load(imgUrl).submit().get());
            }

            builder.setContentTitle(title)
                    .setContentText(body)
                    .setColorized(true)
                    .addAction(android.R.drawable.ic_media_play, "Play", pendingIntent)
                    .setSound(defaultSoundUri)
                    .setColor(ContextCompat.getColor(MyFirebaseMessagingService.this, R.color.teal_200))
                    .setAutoCancel(true)
                    .setSmallIcon(R.drawable.splash_logo)
                    .setStyle(style)
                    .setContentIntent(pendingIntent);
            notificationManager.notify(0 /* ID of notification */, builder.build());

        } catch (Exception e) {
            Log.e("Notification", String.valueOf(e));
        }
    }
}



















