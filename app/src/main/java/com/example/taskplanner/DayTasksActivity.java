package com.example.taskplanner;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

public class DayTasksActivity extends SingleFragmentActivity {

    public static final String EXTRA_DAY_ID = "com.example.taskplanner.day_id";

    public static Intent newIntent(Context packageContext, int dayId){
        Intent intent = new Intent(packageContext,DayTasksActivity.class);
        intent.putExtra(EXTRA_DAY_ID, dayId);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        return new DayTaskFragment();
    }
}
