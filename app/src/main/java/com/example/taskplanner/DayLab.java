package com.example.taskplanner;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.view.View;

import com.example.taskplanner.DayDbScheme.DayTable;
import com.example.taskplanner.DayDbScheme.TaskTable;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

public class DayLab {

    private Context mContext;
    private SQLiteDatabase mDatabase;
    public static final String EXTRA_DATE = "date";
    private static final String EXTRA_DAY_ID = "day_id";
    private AlarmManager mAlarmManager;
    ContentValues mHasNotificationMark = new ContentValues();

    public static Day getDayByID(int id) {
        return null;
    }


    public DayLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new DayBaseHelper(mContext).getWritableDatabase();
        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

    }

    private static ContentValues getDayContentValues(Day day) {
        ContentValues values = new ContentValues();
        values.put(DayTable.Cols.UUID, day.getId());
        values.put(DayTable.Cols.DATE, day.getDate());
        values.put(DayTable.Cols.HAS_TASKS, 0);
        values.put(DayTable.Cols.HAS_NOTIFICATION, 0);
        return values;
    }

    public void addDay(Day day) {
        ContentValues values = getDayContentValues(day);
        mDatabase.insert(DayTable.NAME, null, values);
    }

    private static ContentValues getTaskContentValues(Day day, String task) {
        ContentValues values = new ContentValues();
        values.put(TaskTable.Cols.DAY_ID, day.getId());
        values.put(TaskTable.Cols.TASK, task);
        values.put(TaskTable.Cols.TASK, 0);
        return values;
    }

    public void addTask(Day day, String task) {
        ContentValues values = new ContentValues();
        values.put(TaskTable.Cols.DAY_ID, day.getId());
        values.put(TaskTable.Cols.TASK, task);
        mDatabase.insert(TaskTable.NAME, null, values);
        values.clear();
        values.put(DayTable.Cols.HAS_TASKS, 1);
        mDatabase.update(DayTable.NAME, values, DayTable.Cols.ID + " =?", new String[]{String.valueOf(day.getId())});
    }


    public boolean hasDate(String date) {

        Cursor cursor = mDatabase.query(DayTable.NAME, new String[]{DayTable.Cols.DATE}, DayTable.Cols.DATE + " =?", new String[]{date}, null, null, null);
        boolean hasDate = cursor.getCount() == 1;
        cursor.close();
        return hasDate;
    }

    public int getIdByDate(String date) {

        Cursor cursor = mDatabase.query(DayTable.NAME, new String[]{DayTable.Cols.ID}, DayTable.Cols.DATE + " =?", new String[]{date}, null, null, null);
        cursor.moveToFirst();
        int id = cursor.getInt(cursor.getColumnIndex(DayTable.Cols.ID));
        cursor.close();
        return id;
    }


    public ArrayList<Day> getAllDaysFromDB() {
        ArrayList<Day> days = new ArrayList<>();
        Cursor cursor = mDatabase.query(DayTable.NAME, new String[]{DayTable.Cols.ID, DayTable.Cols.DATE}, null, null, null, null, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                Day day = new Day();

                day.setId(cursor.getInt(cursor.getColumnIndex(DayTable.Cols.ID)));
                day.setDate(cursor.getString(cursor.getColumnIndex((DayTable.Cols.DATE))));
                days.add(0,day);
                cursor.moveToNext();
            }
            while (!cursor.isAfterLast());
            cursor.close();
        }

        return days;
    }


    // TODO: 23.11.2019
    public ArrayList<Day> getDaysWithTaskFromDB() {

        ArrayList<Day> days = new ArrayList<>();
        Cursor cursor = mDatabase.query(DayTable.NAME, new String[]{DayTable.Cols.ID, DayTable.Cols.DATE}, DayTable.Cols.HAS_TASKS + " =?", new String[]{"1"}, null, null, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                Day day = new Day();

                day.setId(cursor.getInt(cursor.getColumnIndex(DayTable.Cols.ID)));
                day.setDate(cursor.getString(cursor.getColumnIndex((DayTable.Cols.DATE))));
                days.add(0, day);
                cursor.moveToNext();
            }
            while (!cursor.isAfterLast());
            cursor.close();
        }

        return days;
    }

    public boolean checkForTasks(Day day) {
        Cursor cursor = mDatabase.query(TaskTable.NAME, new String[]{TaskTable.Cols.DAY_ID}, TaskTable.Cols.DAY_ID + " =?", new String[]{String.valueOf(day.getId())}, null, null, null);
        cursor.moveToFirst();
        if (cursor.getCount() == 0) {
            ContentValues values = new ContentValues();
            values.put(DayTable.Cols.HAS_TASKS, 0);
            mDatabase.update(DayTable.NAME, values, DayTable.Cols.ID + " =?", new String[]{String.valueOf(day.getId())});
            values.clear();
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    public boolean checkForNotification(Day day) {
        Cursor cursor = mDatabase.query(DayTable.NAME, new String[]{DayTable.Cols.HAS_NOTIFICATION}, DayTable.Cols.ID + " =?", new String[]{String.valueOf(day.getId())}, null, null, null);
        cursor.moveToFirst();

        if (cursor.getInt(cursor.getColumnIndex(DayTable.Cols.HAS_NOTIFICATION)) == 1) {
            cursor.close();
            return true;

        } else
            cursor.close();
        return false;
    }


    public ArrayList<Task> getTasksFromDB(int dayID) {
        ArrayList<Task> tasks = new ArrayList<>();
        Cursor cursor = mDatabase.query(TaskTable.NAME, new String[]{TaskTable.Cols.TASK, TaskTable.Cols.ID}, TaskTable.Cols.DAY_ID + " =?", new String[]{String.valueOf(dayID)}, null, null, null);
        String task;
        int id;
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                task = cursor.getString(cursor.getColumnIndex(TaskTable.Cols.TASK));
                id = cursor.getInt(cursor.getColumnIndex(TaskTable.Cols.ID));
                tasks.add(new Task(id, task));
                cursor.moveToNext();
            }
            while (!cursor.isAfterLast());
            cursor.close();
        }
        return tasks;
    }

    public Day dbToDay(String id) {

        Cursor cursor = mDatabase.query(DayTable.NAME, new String[]{DayTable.Cols.ID, DayTable.Cols.DATE}, DayTable.Cols.ID + " =?", new String[]{id}, null, null, null);
        cursor.moveToFirst();
        Day day = new Day();
        ArrayList<String> tasks = new ArrayList<>();
        if (cursor.getCount() > 0) {


            day.setId(cursor.getInt(cursor.getColumnIndex(DayTable.Cols.ID)));
            day.setDate(cursor.getString(cursor.getColumnIndex((DayTable.Cols.DATE))));

        }
        cursor.close();
        return day;
    }


    // удаляет из базы день и его задания
    public void RemoveDay(int id) {
        mDatabase.delete(DayTable.NAME, DayTable.Cols.ID + " =?", new String[]{String.valueOf(id)});
        mDatabase.delete(TaskTable.NAME, TaskTable.Cols.DAY_ID + " =?", new String[]{String.valueOf(id)});
    }

    //удаляет задание
    public void RemoveTask(int id) {

        mDatabase.delete(TaskTable.NAME, TaskTable.Cols.ID + " =?", new String[]{String.valueOf(id)});
    }

    public void updateTask(Task task, String newTask) {
        ContentValues values = new ContentValues();
        values.put(TaskTable.Cols.TASK, newTask);
        mDatabase.update(TaskTable.NAME, values, TaskTable.Cols.ID + " =?", new String[]{String.valueOf(task.getId())});

    }

    public void setNotification() {
        ArrayList<Day> DaysWithTasks = getDaysWithTaskFromDB();

        for (Day day : DaysWithTasks) {
            if (!checkForNotification(day)) {
                markDayNotification(day, true);
                createAlarm(day);
            }


        }

    }

    public void rebuildNotification() {
        ArrayList<Day> DaysWithTasks = getDaysWithTaskFromDB();

        for (Day day : DaysWithTasks) {
            if (checkForNotification(day)) {
                createAlarm(day);
            }


        }
    }

    private void createAlarm(Day day) {
        String dateStr = day.getDate();
        Calendar calendar = new GregorianCalendar();
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SettingsActivity.APP_PREFERENCES, Context.MODE_PRIVATE);
        Long timeInMs = TimeUnit.HOURS.toMillis(sharedPreferences.getLong(SettingsActivity.HOUR_FOR_DAY_NOTIFICATION, 0))
                + TimeUnit.MINUTES.toMillis(sharedPreferences.getLong(SettingsActivity.MINUTE_FOR_DAY_NOTIFICATION, 0));

        int dayId = day.getId();

        try {
            Date date = new SimpleDateFormat(DaysListFragment.DATE_PATTERN).parse(dateStr);
            calendar.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Intent notifyIntent = new Intent(mContext, NotificationReceiver.class);
        notifyIntent.putExtra(DayTasksActivity.EXTRA_DAY_ID, dayId);


        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, day.getId(),
                notifyIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        mAlarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() + timeInMs, pendingIntent);

    }

    public void markDayNotification(Day day, Boolean hasNotification) {
        if (hasNotification) {
            mHasNotificationMark.put(DayTable.Cols.HAS_NOTIFICATION, 1);
        } else {
            mHasNotificationMark.put(DayTable.Cols.HAS_NOTIFICATION, 0);
        }
        mDatabase.update(DayTable.NAME, mHasNotificationMark, DayTable.Cols.ID + " =?", new String[]{String.valueOf(day.getId())});
        mHasNotificationMark.clear();

    }

    public void markDaysNotification(Boolean notify) {
        ArrayList<Day> DaysWithTasks = getDaysWithTaskFromDB();
        for (Day day : DaysWithTasks) {

            markDayNotification(day, notify);
        }
    }

    public void cancelNotification(Day day) {

        Intent notifyIntent = new Intent(mContext, NotificationReceiver.class);
        markDayNotification(day, false);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, day.getId(),

                notifyIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        mAlarmManager.cancel(pendingIntent);
    }

    public void cancelAllNotification() {
        ArrayList<Day> DaysWithTasks = getDaysWithTaskFromDB();
        for (Day day : DaysWithTasks) {
            if (checkForNotification(day)) {
                cancelNotification(day);
            }
        }
    }
}
