package com.example.taskplanner;

public class Task {
    private int id;
    private String Task;

    public Task(int id, String task) {
        this.id = id;
        Task = task;
    }

    public int getId() {
        return id;
    }

    public String getTask() {
        return Task;
    }
}
