/*
 * Copyright (c) 2016. This app was made by Otavio Tarelho and Diego Nunes as requirement to get their major certificate. Any copy of this project will suffer legal penalties under Copyrights Laws.
 */

package com.tempus;

/* Imports section */
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TimePicker;

import java.util.Locale;

public class NewAlarmActivity extends AppCompatPreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_alarm);
        getFragmentManager().beginTransaction().replace(R.id.preferences_alarm,
                new NewAlarmItems()).commit();

        Bundle extras = getIntent().getExtras();
        String come_from = extras.getString("ALARM");

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean hour_system = sharedPref.getBoolean("hour_system", true);
        TimePicker textClock = (TimePicker) findViewById(R.id.timePicker);

        if(hour_system){
            textClock.setIs24HourView(true);
        }
        else{
            textClock.setIs24HourView(false);
        }

        if(come_from.equals(MainActivity.EXTRA_MESSAGE)){
            //Does not do anything
        }
        else if(come_from.equals(MainActivity.EXTRA_MESSAGE_EDIT)) {
            //Get info from ALARM
        }
        else if(come_from.equals(MainActivity.EXTRA_MESSAGE_ADD_EVENT)){
            //Get info from Events
            Intent i = getIntent();
            Event e = (Event) i.getSerializableExtra("DATA");
            // DO SOMETHING WITH DATA
        }
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_alarm, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            if (!super.onMenuItemSelected(featureId, item)) {
                NavUtils.navigateUpFromSameTask(this);
            }
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == R.id.action_save_alarm) {
            saveData();
            Log.e("Save selected", "TRUE");
        }

        return super.onOptionsItemSelected(item);
    } // end of onOptionItemSelected

    public void saveData(){
        //saveData in the database
    }


}

