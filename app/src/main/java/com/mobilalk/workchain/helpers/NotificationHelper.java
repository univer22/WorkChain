package com.mobilalk.workchain.helpers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.mobilalk.workchain.MainActivity;
import com.mobilalk.workchain.R;

public class NotificationHelper {
    private static final String CHANNEL_ID = "WORKCHAIN";
    private final int NOTIFICATION_ID = 0;

    private NotificationManager notifyManager;
    private Context context;


    public NotificationHelper(Context context) {
        this.context = context;
        this.notifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        createChannel();
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            return;

        NotificationChannel channel = new NotificationChannel
                (CHANNEL_ID, "WorkChain", NotificationManager.IMPORTANCE_DEFAULT);

        channel.enableVibration(true);
        channel.setDescription("Értesítés");

        notifyManager.createNotificationChannel(channel);
    }

    public void send(String message) {
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                (int) System.currentTimeMillis(),
                intent,
                 PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("WorkChain")
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setSmallIcon(R.drawable.logo)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        notifyManager.notify((int) System.currentTimeMillis(), builder.build());
    }
}