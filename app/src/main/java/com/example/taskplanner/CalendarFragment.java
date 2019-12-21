package com.example.taskplanner;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnCalendarPageChangeListener;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class CalendarFragment extends Fragment {
    public static final String TAG = "CalendarFragment";
    CalendarView mCalendarView;
    DayLab mDayLab;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mDayLab = new DayLab(getContext());
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        ArrayList<Day> days = mDayLab.getDaysWithTaskFromDB();
        mCalendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {

                Calendar calendar = eventDay.getCalendar();
                Date date = new GregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).getTime();
                String dateFormat = new SimpleDateFormat(DaysListFragment.DATE_PATTERN).format(date);
                if (mDayLab.hasDate(dateFormat)) {
                    int id = mDayLab.getIdByDate(dateFormat);
                    Intent intent = DayTasksActivity.newIntent(getActivity(), id);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(getContext(),"Дата не создана",Toast.LENGTH_SHORT).show();
                }


            }
        });



        mCalendarView.setEvents(createEvents(days));

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.calendar, container, false);
        mCalendarView = view.findViewById(R.id.calendar);
        return view;


    }


    private List<EventDay> createEvents(ArrayList<Day> days) {
        List<EventDay> eventDays = new ArrayList<>();
        Date date;
        for (Day day : days) {
            String stringDate = day.getDate();
            try {
                date = new SimpleDateFormat(DaysListFragment.DATE_PATTERN).parse(stringDate);
                Calendar calendar = new GregorianCalendar();

                calendar.setTime(date);

                eventDays.add(new EventDay(new GregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)), R.drawable.ic_task));
            } catch (ParseException e) {
                e.printStackTrace();

            }

        }
        return eventDays;
    }


}
