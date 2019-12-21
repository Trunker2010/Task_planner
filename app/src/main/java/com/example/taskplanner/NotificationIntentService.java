package com.example.taskplanner;



import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationManagerCompat;

import java.util.Date;

class NotificationIntentService extends JobIntentService {
    private static final int NOTIFICATION_ID = 3;

    Date mDate;

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Intent notifyIntent = new Intent(this, DayTasksActivity.class);
        notifyIntent.putExtra("date", mDate);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 3, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this)
                .setContentTitle("Задания сегодня")
                .setContentText("У Вас езсть задания на сегодня")
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.add_fab);

        Notification notification = builder.build();
        notification.notify();
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(NOTIFICATION_ID, notification);
    }
}
