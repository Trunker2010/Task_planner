package com.example.taskplanner;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.UUID;

public class DayTasksActivity extends SingleFragmentActivity {

    public static final String EXTRA_CREME_ID = "com.example.taskplanner.day_id";

    public static Intent newIntent(Context packageContext, int dayId){
        Intent intent = new Intent(packageContext,DayTasksActivity.class);
        intent.putExtra(EXTRA_CREME_ID, dayId);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        return new DayTaskFragment();
    }
}
