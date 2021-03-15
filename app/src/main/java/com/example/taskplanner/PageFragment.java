package com.example.taskplanner;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;


public class PageFragment extends Fragment {

    public static final String KEY_DAYS_LIST_FRAGMENT = "dlf";
    private FragmentManager mFragmentManager;
    private Fragment mDaysListFragment ,mCalendarFragment;
    private DialogFragment mDatePickerFragment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragmentManager = getChildFragmentManager();
        mDatePickerFragment = DatePickerFragment.newInstance();
        mCalendarFragment = CalendarFragment.newInstance();
        if (savedInstanceState != null) {
            mDaysListFragment = mFragmentManager.getFragment(savedInstanceState, KEY_DAYS_LIST_FRAGMENT);
        } else{
            mDaysListFragment = DaysListFragment.newInstance();
        }


    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.tab_layout, container, false);
        ViewPager viewPager = view.findViewById(R.id.viewpager);
        SampleFragmentPagerAdapter sampleFragmentPagerAdapter = new SampleFragmentPagerAdapter(mFragmentManager, FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPager.setAdapter(sampleFragmentPagerAdapter);
        TabLayout tabLayout = view.findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
        FloatingActionButton FABAddDay = view.findViewById(R.id.fab);
        BottomAppBar bottomAppBar = view.findViewById(R.id.bottom_app_bar);
        bottomAppBar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.settings) {
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);
                return true;
            }
            return true;
        });
        FABAddDay.setOnClickListener(v -> {
            mDatePickerFragment.setTargetFragment(mDaysListFragment, DaysListFragment.REQUEST_DATE);
            mDatePickerFragment.show(mFragmentManager, DaysListFragment.DIALOG_DATE);
        });
        return view;
    }



    public class SampleFragmentPagerAdapter extends FragmentStatePagerAdapter {
        final int PAGE_COUNT = 2;
        private final String[] tabTitles = new String[]{"List", "Calendar"};

        public SampleFragmentPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return mDaysListFragment;
                case 1:
                    return mCalendarFragment;
                default:
                    return null;
            }


        }

        @Override
        public CharSequence getPageTitle(int position) {

            return tabTitles[position];
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {

        mFragmentManager.putFragment(outState, KEY_DAYS_LIST_FRAGMENT, mDaysListFragment);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}


