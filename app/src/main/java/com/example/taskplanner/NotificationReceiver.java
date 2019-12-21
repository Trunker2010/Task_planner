package com.example.taskplanner;


import android.app.NotificationChannel;
import android.app.NotificationManager;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import android.graphics.Color;

import java.time.DayOfWeek;


public class NotificationReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        DayLab dayLab = new DayLab(context);
        final CharSequence NOTIFICATION_CHANNEL_NAME = "Day Notification";

        NotificationManager notificationManager;
        String CHANNEL_ID = "my_channel_01";
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setLockscreenVisibility(importance);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            notificationManager.createNotificationChannel(notificationChannel);
        }
        int dayId = intent.getIntExtra(DayTasksActivity.EXTRA_DAY_ID, 0);
        Day day = dayLab.dbToDay(String.valueOf(dayId));

        Intent resultIntent = DayTasksActivity.newIntent(context, dayId);

        //resultIntent.putExtra(DayTasksActivity.EXTRA_DAY_ID, dayId);


        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)

                .setTicker("На " + day.getDate() + " есть дела")
                .setSmallIcon(R.drawable.baseline_calendar)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setWhen(System.currentTimeMillis()).setChannelId(CHANNEL_ID)
                .setContentTitle("На " + day.getDate() + " есть дела") // Заголовок уведомления
                .setContentIntent(PendingIntent.getActivity(context, dayId, resultIntent, PendingIntent.FLAG_CANCEL_CURRENT))
                .setContentText("Не забудеь про запланированные дела"); // Текст;


        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify((int) (System.currentTimeMillis() / 1000), mBuilder.build());

    }
}

