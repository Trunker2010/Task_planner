package com.example.taskplanner;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.util.Log;
import android.widget.Toast;

public class setNotificationReceiver extends BroadcastReceiver {
    DayLab mDayLab;

    public setNotificationReceiver() {
    }

    String TAG = "setNotificationReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        mDayLab = new DayLab(context);

        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            if (context.getSharedPreferences(SettingsActivity.APP_PREFERENCES,Context.MODE_PRIVATE).getBoolean(SettingsFragment.NOTIFICATION_IS_CHECKED, false)) {
                mDayLab.rebuildNotification();
            }

            Log.d(TAG, "onReceive");
            // ваш код здесь
        }

    }
}