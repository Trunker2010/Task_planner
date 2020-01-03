package com.example.taskplanner;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.Calendar;

public class PageFragment extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";
    private FloatingActionButton FABAddDay;
    BottomAppBar mBottomAppBar;
    private int mPage;
    SampleFragmentPagerAdapter mSampleFragmentPagerAdapter;
    Fragment mDaysListFragment;

    public static PageFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        PageFragment fragment = new PageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_layout, container, false);

        ViewPager viewPager = view.findViewById(R.id.viewpager);

        mSampleFragmentPagerAdapter =new SampleFragmentPagerAdapter(getFragmentManager(), getActivity());
        viewPager.setAdapter(mSampleFragmentPagerAdapter);
        mDaysListFragment = DaysListFragment.newInstance();

        // Передаём ViewPager в TabLayout
        TabLayout tabLayout = view.findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
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

                datePickerFragment.setTargetFragment( mDaysListFragment, DaysListFragment.REQUEST_DATE);
                datePickerFragment.show(fragmentManager, DaysListFragment.DIALOG_DATE);
            }
        });
        return view;
    }

    public class SampleFragmentPagerAdapter extends FragmentPagerAdapter {
        final int PAGE_COUNT = 2;
        private String tabTitles[] = new String[]{"List", "Calendar"};
        private Context context;

        public SampleFragmentPagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.context = context;
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public androidx.fragment.app.Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return mDaysListFragment;
                case 1:
                    return new CalendarFragment();
                default:
                    return null;
            }


        }

        @Override
        public CharSequence getPageTitle(int position) {
            // генерируем заголовок в зависимости от позиции
            return tabTitles[position];
        }
    }
}


