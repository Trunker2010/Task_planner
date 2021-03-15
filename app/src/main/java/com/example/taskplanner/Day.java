package com.example.taskplanner;





import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class Day {
  
    private int mId;
    private String mDate;
    private ArrayList<String> mTasks;

    public int getId() {
        return mId;
    }

    //private UUID mId;


    public void setDate(String date) {
        mDate = date;
    }




    public void setId(int id) {
        mId = id;
    }












    public Day() {
        mTasks = new ArrayList<>();
    }

    public void addTask(String task) {
        mTasks.add(task);
    }

    public ArrayList<String> getTasks() {
        return mTasks;
    }

    public String getDate() {
        return mDate;
    }
}
