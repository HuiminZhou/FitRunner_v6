package com.example.huimin_zhou.Huimin_Zhou_FitRunner;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v13.app.FragmentCompat;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.design.widget.TabLayout;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ViewGroup;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private MyFragmentAdapter mFragmentPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermission(); // check permission of location

        // set toolbar, view pager and tablayout
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ViewPager viewPager = (ViewPager)findViewById(R.id.view_pager);
        viewPager.setOffscreenPageLimit(3); // set the number of segments as 3
        mFragmentPagerAdapter = new MyFragmentAdapter(getFragmentManager(),
                getFragments(R.id.view_pager));

        viewPager.setAdapter(mFragmentPagerAdapter);

        TabLayout tabLayout = (TabLayout)findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout) {
            @Override
            public void onPageSelected(int position) {
                if (position == Global.POS_HISTORY) {
                    ((HistoryFragment) mFragmentPagerAdapter.getItem(1))
                            .updateAdapter(MainActivity.this);
                }
            }
        });
    }

    public void checkPermission() {
        if (Build.VERSION.SDK_INT < 23) {
            return;
        }
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }
    }

    private ArrayList<Fragment> getFragments(int viewId) {
        ArrayList<Fragment> fragments = new ArrayList<>();
        FragmentManager fm = getFragmentManager();
        WelcomeFragment welcome;
        HistoryFragment history;
        SettingFragment setting;
        // if there is old fragment, just use the old ones, otherwise create new
        if ((welcome = (WelcomeFragment) fm.findFragmentByTag("android:switcher:" + viewId + ":0"))
                == null) {
            welcome = new WelcomeFragment();
        }
        if ((history = (HistoryFragment) fm.findFragmentByTag("android:switcher:" + viewId + ":1"))
                == null) {
            history = new HistoryFragment();
        }
        if ((setting = (SettingFragment) fm.findFragmentByTag("android:switcher:" + viewId + ":2"))
                == null) {
            setting = new SettingFragment();
        }

        fragments.add(welcome);
        fragments.add(history);
        fragments.add(setting);
        return fragments;
    }

    public class MyFragmentAdapter extends FragmentPagerAdapter {
        private ArrayList<Fragment> mFragments = null;

        public MyFragmentAdapter(android.app.FragmentManager fm, ArrayList<Fragment> fragments) {
            super(fm);
            this.mFragments = fragments;
        }

        @Override
        public Fragment getItem(int pos) {
            return mFragments.get(pos);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int pos) {
            if (pos == Global.POS_WELCOME) {
                return "Welcome";
            } else if (pos == Global.POS_HISTORY) {
                return "History";
            } else if (pos == Global.POS_SETTING) {
                return "Setting";
            } else {
                return null;
            }
        }
    }
}