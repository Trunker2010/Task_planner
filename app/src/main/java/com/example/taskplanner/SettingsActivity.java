package com.example.taskplanner;

import androidx.fragment.app.Fragment;

public class SettingsActivity extends SingleFragmentActivity {
    public static final String APP_PREFERENCES = "settings";
    public static final String TIME_FOR_DAY_NOTIFICATION = "time_for_day_notification";
    public static final String HOUR_FOR_DAY_NOTIFICATION = "hour_for_day_notification";
    public static final String MINUTE_FOR_DAY_NOTIFICATION ="minute_for_day_notification" ;

    @Override
    protected Fragment createFragment() {
        return new SettingsFragment();
    }
}
