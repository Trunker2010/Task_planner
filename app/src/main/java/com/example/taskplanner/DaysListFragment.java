package com.example.taskplanner;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.applandeo.materialcalendarview.EventDay;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class DaysListFragment extends Fragment {
    private static final String CALENDAR_TAG = "calendar";
    private RecyclerView mDayRecyclerView;
    private RecyclerView mTaskRecyclerView;
    private FloatingActionButton FABAddDay;
    public static final String DIALOG_DATE = "dialogDate";
    public static final int REQUEST_DATE = 0;
    public static final String LogTag = "DaysListFragment";
    DayLab mDayLab;
    BottomAppBar mBottomAppBar;
    ArrayList<Day> days;
    public static final String DATE_PATTERN = "d MMMM yyyy, E";

    DayAdapter mDayAdapter;
    Calendar dateAndTime = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateAndTime.set(Calendar.YEAR, year);
            dateAndTime.set(Calendar.MONTH, monthOfYear);
            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        }
    };

    public static Fragment newInstance() {
        DaysListFragment fragment = new DaysListFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.days_fragment, container, false);
        mDayRecyclerView = view.findViewById(R.id.RVDays);
        mTaskRecyclerView = view.findViewById(R.id.RVTasks);
        FABAddDay = view.findViewById(R.id.fab);
        mBottomAppBar = view.findViewById(R.id.bottom_app_bar);

        mBottomAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.settings: {
                        Intent intent = new Intent(getActivity(), SettingsActivity.class);
                        startActivity(intent);
                        return true;
                    }
                    default:
                        return true;
                }

            }
        });
        FABAddDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                DatePickerFragment datePickerFragment = new DatePickerFragment();
                datePickerFragment.setTargetFragment(DaysListFragment.this, REQUEST_DATE);
                datePickerFragment.show(fragmentManager, DIALOG_DATE);
            }
        });

        mDayRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateRecyclerView();

        return view;
    }


    private class DayHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView date;
        Day mDay;
        Button mRemoveMaterialButton;


        public DayHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.day_item_list, parent, false));
            date = itemView.findViewById(R.id.TVDate);
            mRemoveMaterialButton = itemView.findViewById(R.id.removeButton);
            mRemoveMaterialButton.setOnClickListener(this);
            itemView.setOnClickListener(this);

        }

        public void bind(Day day) {
            mDay = day;
            date.setText(day.getDate());
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.card_view:
                    Intent intent = DayTasksActivity.newIntent(getActivity(), mDay.getId());
                    startActivity(intent);
                    return;
                case R.id.removeButton:
                    mDayLab.RemoveDay(mDay.getId());
                    updateRecyclerView();
                    mDayLab.checkForTasks(mDay);
                    mDayLab.cancelNotification(mDay);
                    return;

            }

        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_DATE) {


            Day day = new Day();
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            String dateFormat = new SimpleDateFormat(DATE_PATTERN).format(date);


            if (mDayLab.hasDate(dateFormat)) {
                Log.d(LogTag, "Дата Есть");
                Context context;
                CharSequence text;
                Toast toast = Toast.makeText(getContext(), "Дата уже создана", Toast.LENGTH_LONG);
                toast.show();
                Intent intent = DayTasksActivity.newIntent(getActivity(), mDayLab.getIdByDate(dateFormat));
                startActivity(intent);
                return;
            }
            day.setDate(dateFormat);
            mDayLab.addDay(day);
            Log.d(LogTag, "Дата Добавлена");
            updateRecyclerView();

        }
    }

    public class DayAdapter extends RecyclerView.Adapter<DayHolder> {
        private List<Day> mDays;

        public void setDays(List<Day> days) {
            mDays = days;
        }


        public DayAdapter(List<Day> days) {
            mDays = days;
        }

        @NonNull
        @Override
        public DayHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new DayHolder(layoutInflater, parent);

        }

        @Override
        public void onBindViewHolder(@NonNull DayHolder holder, int position) {
            Day day = mDays.get(position);

            holder.bind(day);
        }

        @Override
        public int getItemCount() {
            return mDays.size();
        }
    }

    private void updateRecyclerView() {

        days = mDayLab.getAllDaysFromDB();
        if (mDayAdapter == null) {
            mDayAdapter = new DayAdapter(days);
            mDayRecyclerView.setAdapter(mDayAdapter);
        } else {
            mDayAdapter.setDays(days);
            mDayAdapter.notifyDataSetChanged();
        }

    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDayLab = new DayLab(getActivity());
    }


}
