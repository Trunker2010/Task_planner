package com.example.taskplanner;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.taskplanner.DayDbScheme.DayTable;
import com.example.taskplanner.DayDbScheme.TaskTable;

public class DayBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    public static final String DATA_BASE_NAME = "DayBase.db";

    public DayBaseHelper(Context context) {
        super(context, DATA_BASE_NAME, null, VERSION);

    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + DayTable.NAME + "( " +
                DayTable.Cols.ID + " INTEGER primary key autoincrement," +
                DayTable.Cols.UUID + ", " +
                DayTable.Cols.DATE + ", " +
                DayTable.Cols.HAS_NOTIFICATION + "  INTEGER NOT NULL"+", "+
                DayTable.Cols.HAS_TASKS + "  INTEGER NOT NULL" +
                ")"
        );


        db.execSQL(String.format("CREATE TABLE %s ( %s INTEGER primary key autoincrement, %s , %s INTEGER, FOREIGN KEY (%s) REFERENCES %s ( %s ) );",
                TaskTable.NAME, TaskTable.Cols.ID, TaskTable.Cols.TASK, TaskTable.Cols.DAY_ID, TaskTable.Cols.DAY_ID, DayTable.NAME, DayTable.Cols.ID)


        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
