/*
 * Copyright (c) 2016. This app was made by Otavio Tarelho and Diego Nunes as requirement to get their major certificate. Any copy of this project will suffer legal penalties under Copyrights Laws.
 */

package com.tempus;

/* Imports section */
import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.tempus.Alarm.AlarmFragment;
import com.tempus.Alarm.NewAlarmActivity;
import com.tempus.Events.EventFragment;
import com.tempus.Preferences.TempusSettingsActivity;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public final static String EXTRA_MESSAGE = "com.tempus.NEW";
    public final static String EXTRA_MESSAGE_EDIT = "com.tempus.EDIT";
    public final static String EXTRA_MESSAGE_ADD_EVENT = "com.tempus.EVENT";
    public static final String SAVE_ALARM_LIST = "Alarmes"; // TAG
    public static final String SAVE_EVENT_LIST = "Eventos"; // TAG
    private static final int MY_PERMISSIONS = 5;
    private final int[] ICON = {
            R.drawable.ic_alarm_white_36dp,
            R.drawable.ic_event_white_36dp,
            R.drawable.ic_pie_chart_white_36dp
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SectionAdapter mSectionsPagerAdapter;
        ViewPager mViewPager;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mSectionsPagerAdapter = new SectionAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        for(int i = 0; i < 2; i++) {
            tabLayout.getTabAt(i).setIcon(ICON[i]);
        }

    }

    public void restartIntent(){
        Intent intent = this.getIntent();
        finish();
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        String lang = settings.getString("lang_setting", "");
        Configuration config = getBaseContext().getResources().getConfiguration();
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        config.setLocale(locale);
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        super.onResume();

        if((ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_DENIED )
                || (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED )
                || (ContextCompat.checkSelfPermission(this,
                Manifest.permission.INTERNET) == PackageManager.PERMISSION_DENIED)) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_CALENDAR,
                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.INTERNET},
                    MY_PERMISSIONS);

            restartIntent();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         int id = item.getItemId();

        if(id == R.id.action_add_new_alarm){
            Intent newAlarm = new Intent(this, NewAlarmActivity.class);
            newAlarm.putExtra("ALARM", EXTRA_MESSAGE);
            startActivity(newAlarm);
            return true;
        }

        else if (id == R.id.action_settings) {
            Intent settings = new Intent(this, TempusSettingsActivity.class);
            startActivity(settings);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    public class SectionAdapter extends FragmentPagerAdapter {

        private SectionAdapter(FragmentManager fm) { super(fm); }

        @Override
        public Fragment getItem(int position) {
            switch(position) {
                case 0:
                    return AlarmFragment.newInstance();
                case 1:
                    return EventFragment.newInstance();
            }

            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }
    }

}
