/*
 * Copyright (c) 2016. This app was made by Otavio Tarelho and Diego Nunes as requirement to get their major certificate. Any copy of this project will suffer legal penalties under Copyrights Laws.
 */

package com.tempus.Alarm;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TimePicker;
import android.widget.Toast;
import com.tempus.Events.Event;
import com.tempus.MainActivity;
import com.tempus.Preferences.AppCompatPreferenceActivity;
import com.tempus.R;
import java.util.Locale;

public class NewAlarmActivity extends AppCompatPreferenceActivity {

    private AlertDialog confirmDialogObj;

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
        SharedPreferences.Editor edit = sharedPref.edit();
        ClearPreferences(edit);
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
            buildConformDialog();
            confirmDialogObj.show();
        }

        return super.onOptionsItemSelected(item);
    } // end of onOptionItemSelected

    public void saveData(){
        //saveData in the database

        // return to home
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

        //Set toast mensage
        Context context = getApplicationContext();
        CharSequence text = getResources().getString(R.string.save_alarm_toast);
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();

        //Finish this activity
        finish();
    }

    private void buildConformDialog(){
        AlertDialog.Builder confirmBuilder = new AlertDialog.Builder(this);
        confirmBuilder.setTitle(R.string.save_alarm_title);
        confirmBuilder.setMessage(R.string.save_alarm_sum);
        confirmBuilder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which){
                saveData();
            }
        });

        confirmBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which){
            }
        });

        confirmDialogObj = confirmBuilder.create();
    }

    public void ClearPreferences(SharedPreferences.Editor edit){
        edit.remove("alarm_name");
        edit.remove("alarm_ringtone");
        edit.remove("alarm_type");
        edit.remove("alarm_snooze");
        edit.remove("alarm_repeat");
        edit.remove("event_location");
        edit.remove("event_start_time");
        edit.remove("event_end_time");
        edit.commit();
    }

}

