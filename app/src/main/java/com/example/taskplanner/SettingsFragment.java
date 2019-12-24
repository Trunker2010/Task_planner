package com.example.taskplanner;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.switchmaterial.SwitchMaterial;

import static android.provider.AlarmClock.EXTRA_HOUR;
import static android.provider.AlarmClock.EXTRA_MINUTES;

public class SettingsFragment extends Fragment {
    public static final int Time_RCode = 1;
    public static final String DIALOG_TIME = "com.example.taskplanner.TimePickerFragment";
    public static final String NOTIFICATION_IS_CHECKED = "com.example.taskplanner.SettingsFragment.NOTIFICATION_IS_CHECKED";
    TextView timeTextView;
    SharedPreferences mSharedPreferences;
    SwitchMaterial notificationSwitch;
    DayLab mDayLab;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings_fragment, container, false);
        timeTextView = view.findViewById(R.id.textViewTime);
        notificationSwitch = view.findViewById(R.id.notificationSwitch);
        notificationSwitch.setChecked(mSharedPreferences.getBoolean(NOTIFICATION_IS_CHECKED, false));
        updateTimeTextView();
        if (!notificationSwitch.isChecked()) {
            timeTextView.setVisibility(View.INVISIBLE);
        }
        notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {


            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    FragmentManager fragmentManager = getFragmentManager();
                    TimePickerFragment timePickerFragment = new TimePickerFragment();
                    timePickerFragment.setTargetFragment(SettingsFragment.this, Time_RCode);
                    timePickerFragment.show(fragmentManager, DIALOG_TIME);
                    mSharedPreferences.edit()
                            .putBoolean(NOTIFICATION_IS_CHECKED, true)
                            .apply();


                } else {
                    mSharedPreferences.edit()
                            .putBoolean(NOTIFICATION_IS_CHECKED, false)
                            .apply();
                    mDayLab.cancelAllNotification();
                    mDayLab.markDaysNotification(false);

                    timeTextView.setVisibility(View.INVISIBLE);
                }
            }
        });
        return view;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mSharedPreferences = getActivity().getSharedPreferences(SettingsActivity.APP_PREFERENCES, Context.MODE_PRIVATE);
        mDayLab = new DayLab(getContext());
        super.onCreate(savedInstanceState);


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            notificationSwitch.setChecked(false);

            return;

        }
        if (requestCode == Time_RCode) {
            int hour = data.getIntExtra(EXTRA_HOUR, -1);
            int minute = data.getIntExtra(EXTRA_MINUTES, -1);
            mSharedPreferences.edit()
                    .putLong(SettingsActivity.HOUR_FOR_DAY_NOTIFICATION, hour)
                    .putLong(SettingsActivity.MINUTE_FOR_DAY_NOTIFICATION, minute)
                    .apply();
            updateTimeTextView();
            timeTextView.setVisibility(View.VISIBLE);
        }
    }

    private void updateTimeTextView() {
        if (mSharedPreferences != null) {
            String time = (String.format("%02d", mSharedPreferences.getLong(SettingsActivity.HOUR_FOR_DAY_NOTIFICATION, 0)) + ":" + String.format("%02d", mSharedPreferences.getLong(SettingsActivity.MINUTE_FOR_DAY_NOTIFICATION, 0)));
            timeTextView.setText(time);
        }
    }
}
