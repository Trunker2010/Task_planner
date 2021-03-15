package com.example.taskplanner;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class DaysListFragment extends Fragment {
    private RecyclerView mDayRecyclerView;
    BottomAppBar mBottomAppBar;
    FloatingActionButton mFloatingActionButton;
    public static final String DIALOG_DATE = "dialogDate";
    public static final int REQUEST_DATE = 0;
    public static final String LogTag = "DaysListFragment";
    CoordinatorLayout mCoordinatorLayout;
    DayLab mDayLab;
    List<Day> mDays;
    NestedScrollView mNestedScrollView;
    FrameLayout mFrameLayoutMarcDate;

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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDayLab = new DayLab(getActivity());
    }

    public static Fragment newInstance() {
        DaysListFragment fragment = new DaysListFragment();

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.days_fragment, container, false);
        mDayRecyclerView = view.findViewById(R.id.RVDays);
        mBottomAppBar = getActivity().findViewById(R.id.bottom_app_bar);
        mCoordinatorLayout = view.findViewById(R.id.days_CoordinatorLayout);
        mNestedScrollView = view.findViewById(R.id.nested_scroll_recycler_view);
        mFloatingActionButton = getActivity().findViewById(R.id.fab);
        mBottomAppBar.performShow();
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
            mFrameLayoutMarcDate = itemView.findViewById(R.id.marcDate);
            mRemoveMaterialButton.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mDayAdapter.deleteRow(mDay, getLayoutPosition());
                    return true;
                }
            });


            itemView.setOnClickListener(this);
        }

        public void bind(Day day) {
            mDay = day;
            date.setText(day.getDate());
            if (mDayLab.checkForTasks(day)) {
                mFrameLayoutMarcDate.setVisibility(View.VISIBLE);
            } else
                mFrameLayoutMarcDate.setVisibility(View.INVISIBLE);
        }


        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.card_view:
                    Intent intent = DayTasksActivity.newIntent(getActivity(), mDay.getId());
                    startActivity(intent);


                    return;

            }

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateRecyclerView();
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
                Toast toast = Toast.makeText(getContext(), "Дата уже создана", Toast.LENGTH_LONG);
                toast.show();
                Intent intent = DayTasksActivity.newIntent(getActivity(), mDayLab.getIdByDate(dateFormat));
                startActivity(intent);
                return;
            }

            day.setDate(dateFormat);
            Log.d(LogTag, "Дата Добавлена");
            mDayAdapter.addRow(day);


        }
    }

    public class DayAdapter extends RecyclerView.Adapter<DayHolder> {


        public void setDays() {
            mDays.clear();
            mDays.addAll(mDayLab.getAllDaysFromDB());
        }


        public void deleteRow(Day day, int layoutPosition) {
            mDayLab.RemoveDay(day.getId());
            mDayLab.checkForTasks(day);
            mDayLab.cancelNotification(day);
            mBottomAppBar.performShow();
            setDays();
            notifyItemRemoved(layoutPosition);

        }

        public void addRow(Day day) {

            mDayLab.addDay(day);

            setDays();
            notifyItemInserted(0);


        }


        DayAdapter(List<Day> days) {
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
        public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
            super.onDetachedFromRecyclerView(recyclerView);
        }

        @Override
        public int getItemCount() {
            return mDays.size();
        }


    }


    private void updateRecyclerView() {

        mDays = mDayLab.getAllDaysFromDB();
        if (mDayAdapter == null) {
            mDayAdapter = new DayAdapter(mDays);

        } else {
            mDayRecyclerView.setAdapter(mDayAdapter);
            mDayAdapter.setDays();
            mDayAdapter.notifyDataSetChanged();
        }

    }


}
