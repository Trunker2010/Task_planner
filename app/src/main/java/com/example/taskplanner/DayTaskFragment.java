package com.example.taskplanner;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DayTaskFragment extends Fragment {

    //public static final String DAY_TAG = "day_id";
    public static final String EXTRA_DATE = "date";
    private static final String EXTRA_DAY_ID = "day_id";
    RecyclerView mTaskRecyclerView;
    Day mDay;

    TaskAdapter mTaskAdapter;
    ArrayList<Task> mTasksList;
    DayLab mDayLab;
    EditText taskET;
    Button addTaskBTN;
    TextView dateTV;
    ScrollView scrollView;
    AlarmManager alarmManager;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String dayId = String.valueOf(getActivity().getIntent().getIntExtra(DayTasksActivity.EXTRA_DAY_ID, 0));
        mDayLab = new DayLab(getContext());
        mDay = mDayLab.dbToDay(dayId);
//        alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        setHasOptionsMenu(true);

        getActivity().setTitle(mDay.getDate());
    }

    @Nullable
    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.day_task_fragment, container, false);
        mTaskRecyclerView = v.findViewById(R.id.RWTasksOnDay);
        mTaskRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        taskET = v.findViewById(R.id.taskET);
        addTaskBTN = v.findViewById(R.id.addTaskBTN);
        dateTV = v.findViewById(R.id.dateTV);
        scrollView = v.findViewById(R.id.scrollView);
        dateTV.setText(mDay.getDate());


        addTaskBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String task = String.valueOf(taskET.getText());
                mDayLab.addTask(mDay, task);
                updateRecyclerView();
                mTaskRecyclerView.smoothScrollToPosition(mTasksList.size());

            }
        });
        taskET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    addTaskBTN.setEnabled(true);
                } else {
                    addTaskBTN.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        updateRecyclerView();
        return v;
    }

    private class TaskHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mTaskTextView;
        Button mRemoveButton;
        Button mEditButton;
        Button mOkButton;
        TextView mEditTaskTextView;

        Task mTask;

        public TaskHolder(LayoutInflater inflater, ViewGroup group) {
            super(inflater.inflate(R.layout.task_item_list, group, false));
            mTaskTextView = itemView.findViewById(R.id.TVTask);
            mRemoveButton = itemView.findViewById(R.id.removeTaskBtn);
            mEditButton = itemView.findViewById(R.id.editTaskButton);
            mOkButton = itemView.findViewById(R.id.ok);
            mEditTaskTextView = itemView.findViewById(R.id.editTask);
            itemView.setOnClickListener(this);
            mEditButton.setOnClickListener(this);
            mOkButton.setOnClickListener(this);
            mRemoveButton.setOnClickListener(this);
            initialVisibility();

        }

        public void initialVisibility() {
            mRemoveButton.setVisibility(View.VISIBLE);
            mEditButton.setVisibility(View.VISIBLE);
            mOkButton.setVisibility(View.GONE);
            mEditTaskTextView.setVisibility(View.GONE);
            mTaskTextView.setVisibility(View.VISIBLE);
        }

        public void bind(Task task) {
            mTask = task;
            mTaskTextView.setText(task.getTask());
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.removeTaskBtn:
                    mDayLab.RemoveTask(mTask.getId());
                    mDayLab.checkForTasks(mDay);
                    if (!mDayLab.checkForTasks(mDay)) {
                        mDayLab.cancelNotification(mDay);
                    }
                    updateRecyclerView();
                    return;
                case R.id.editTaskButton:
                    mRemoveButton.setVisibility(View.GONE);
                    mEditButton.setVisibility(View.GONE);
                    mOkButton.setVisibility(View.VISIBLE);
                    mEditTaskTextView.setVisibility(View.VISIBLE);
                    mEditTaskTextView.setText(mTask.getTask());
                    mTaskTextView.setVisibility(View.GONE);

                    return;
                case R.id.ok: {
                    String newTask = String.valueOf(mEditTaskTextView.getText());
                    mDayLab.updateTask(mTask, newTask);
                    updateRecyclerView();
                    initialVisibility();

                    return;
                }
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.day_task_fragment, menu);
    }

    @Override
    public void onStop() {
        super.onStop();
        mDayLab.setNotification();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_task:
                mDayLab.addTask(mDay, "Задача");

                updateRecyclerView();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void updateRecyclerView() {

        mTasksList = mDayLab.getTasksFromDB(mDay.getId());
        if (mTaskAdapter == null) {
            mTaskAdapter = new TaskAdapter(mTasksList);
            mTaskRecyclerView.setAdapter(mTaskAdapter);
        } else {
            mTaskAdapter.setTasks(mTasksList);
            mTaskAdapter.notifyDataSetChanged();
        }

    }

    private class TaskAdapter extends RecyclerView.Adapter<TaskHolder> {
        ArrayList<Task> mTasks;

        public TaskAdapter(ArrayList<Task> tasks) {
            mTasks = tasks;
        }

        @NonNull
        @Override
        public TaskHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // Context context;
            LayoutInflater inflater = LayoutInflater.from(getActivity());

            return new TaskHolder(inflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull TaskHolder holder, int position) {
            Task task = mTasks.get(position);
            holder.bind(task);
        }

        @Override
        public int getItemCount() {
            return mTasks.size();
        }

        public void setTasks(ArrayList<Task> tasksList) {
            mTasks = tasksList;
        }
    }


}
