package com.example.huimin_zhou.fitrunner;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.design.widget.TabLayout;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.example.huimin_zhou.fitrunner.Util.Global;
import com.example.huimin_zhou.fitrunner.backend.registration.Registration;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity {
    private MyFragmentAdapter mFragmentPagerAdapter;
    public static HistoryFragment history;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermission(); // check permission of location

        new GcmRegistrationAsyncTask(this).execute(); // register

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
                            .updateAdapter();
                }
            }
        });
    }

    public void checkPermission() {
        if (Build.VERSION.SDK_INT < 23) return;
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }
    }

    private ArrayList<Fragment> getFragments(int viewId) {
        ArrayList<Fragment> fragments = new ArrayList<>();
        FragmentManager fm = getFragmentManager();
        WelcomeFragment welcome;
        //HistoryFragment history;
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

    //public static final String SERVER_ADDR = "http://192.168.1.143:8080";
    public static final String SERVER_ADDR = "https://infra-signifier-158019.appspot.com/";

    class GcmRegistrationAsyncTask extends AsyncTask<Void, Void, String> {
        private Registration regService = null;
        private GoogleCloudMessaging gcm;
        private Context context;

        private static final String SENDER_ID = "543260771924";

        public GcmRegistrationAsyncTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(Void... params) {
            if (regService == null) {
                Registration.Builder builder = new Registration.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                        .setRootUrl(SERVER_ADDR + "/_ah/api/");
                // end of optional local run code
                regService = builder.build();
            }

            String msg = "";
            try {
                if (gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(context);
                }
                String regId = gcm.register(SENDER_ID);
                msg = "Device registered, registration ID=" + regId;

                // You should send the registration ID to your server over HTTP,
                // so it can use GCM/HTTP or CCS to send messages to your app.
                // The request to your server should be authenticated if your app
                // is using accounts.
                regService.register(regId).execute();

            } catch (IOException ex) {
                ex.printStackTrace();
                msg = "Error: " + ex.getMessage();
            }
            return msg;
        }

        @Override
        protected void onPostExecute(String msg) {
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
            Logger.getLogger("REGISTRATION").log(Level.INFO, msg);
        }
    }
}