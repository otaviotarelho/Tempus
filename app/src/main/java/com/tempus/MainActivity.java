/*
 * Copyright (c) 2016. This app was made by Otavio Tarelho and Diego Nunes as requirement to get their major certificate. Any copy of this project will suffer legal penalties under Copyrights Laws.
 */

package com.tempus;

/* Imports section */
import android.*;
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
import com.tempus.Preferences.AppCompatPreferenceActivity;
import com.tempus.Preferences.TempusSettingsActivity;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public final static String EXTRA_MESSAGE = "com.tempus.NEW";
    public final static String EXTRA_MESSAGE_EDIT = "com.tempus.EDIT";
    public final static String EXTRA_MESSAGE_ADD_EVENT = "com.tempus.EVENT";


    private SectionAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    private final int[] ICON = {
            R.drawable.ic_alarm_white_36dp,
            R.drawable.ic_event_white_36dp,
            R.drawable.ic_pie_chart_white_36dp
    }; // ICON LIST

    public static final String SAVE_ALARM_LIST = "Alarmes"; // TAG
    public static final String SAVE_EVENT_LIST = "Eventos"; // TAG
    private static final int MY_PERMISSIONS = 0005;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        //Set icon to tablayout
        for(int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {

            tabLayout.getTabAt(i).setIcon(ICON[i]);

        } // end of for

    } // end of onCreate

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
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        super.onResume();

        //Get Permission to access database in the first usage
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    } // end of onCreateOptionMenu

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
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
    } // end of onOptionItemSelected



    public class SectionAdapter extends FragmentPagerAdapter {

        public SectionAdapter(FragmentManager fm) { super(fm); } // end of constructor

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            switch(position) {
                case 0:
                    return AlarmFragment.newInstance();
                case 1:
                    return EventFragment.newInstance();
            }

            return null;
        } // end of getItem method

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        } // end of getCount method

        //Method that set title based on the file String.XML in resource folder
        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        } // end of getPageTitle method

    } // end of SectionAdapter method

}
