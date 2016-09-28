/*
 * Copyright (c) 2016. This app was made by Otavio Tarelho and Diego Nunes as requirement to get their major certificate. Any copy of this project will suffer legal penalties under Copyrights Laws.
 */

package com.tempus.Alarm;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
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

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class NewAlarmActivity extends AppCompatPreferenceActivity {

    private AlertDialog confirmDialogObj;
    private Alarm a;
    private Event e;
    private String come_from;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_alarm);
        getFragmentManager().beginTransaction().replace(R.id.preferences_alarm,
                new NewAlarmItems()).commit();

        Bundle extras = getIntent().getExtras();
        come_from = extras.getString("ALARM");

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
            e = (Event) i.getSerializableExtra("DATA");
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
        int id = item.getItemId();

        if(id == R.id.action_save_alarm) {
            buildConformDialog();
            confirmDialogObj.show();
        }

        return super.onOptionsItemSelected(item);
    } // end of onOptionItemSelected

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void saveData() {
        //saveData in the database
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        HashSet<String> to_solve = new HashSet<>();

        TimePicker alarmTime = (TimePicker) findViewById(R.id.timePicker);
        String time;
        time = getStringTime(alarmTime.getCurrentMinute(), alarmTime.getCurrentHour());

        String title = settings.getString("alarm_name", "");
        Set<String> repeat = settings.getStringSet("alarm_repeat", to_solve);
        String ringtone = settings.getString("alarm_ringtone", "");
        Boolean snooze = settings.getBoolean("alarm_snooze", true);
        String type = settings.getString("alarm_type", "");

        String time_event = String.valueOf(settings.getLong("event_start_time", 0));
        String time_event_end = String.valueOf(settings.getLong("event_end_time", 0));
        String event_location = settings.getString("event_location", "");

        //Hardcoded ALARMS
        if(type == "0") {
            // in case new normal alarm
            e = new Event();
            a = new Alarm(title, String.valueOf(R.string.normal_alarm),
                    time, ringtone,repeat,type,snooze, true, e);
            AlarmFragment.alarms.add(a);
        }
        else {

            if(come_from.equals(MainActivity.EXTRA_MESSAGE_ADD_EVENT)) {
                //in case new alarm from events tab
                e.setDay_start(time_event);
                e.setDay_end(time_event_end);
                a = new Alarm(title, String.valueOf(ExpectedTimeOfArrivel(event_location))
                        + R.string.hour, time, ringtone,repeat,type,snooze, true, e);
                AlarmFragment.alarms.add(a);
            }
            else {
                //in case new alarm from menu and traffic based
                e = new Event();
                e.setDay_start(time_event);
                e.setDay_end(time_event_end);
                e.setLocation(event_location);
                e.setName(title);
                a = new Alarm(title, String.valueOf(ExpectedTimeOfArrivel(event_location))
                        + R.string.hour, time, ringtone,repeat,type,snooze, true, e);
                AlarmFragment.alarms.add(a);
            }

        }

        // return to home
        endIntentToMain();
        //Set toast mensage
        toastMessage();
        //Finish this activity
        finish();
    }

    private int ExpectedTimeOfArrivel(String event_location) {
        return 0;
    }

    private void toastMessage(){
        Context context = getApplicationContext();
        CharSequence text = getResources().getString(R.string.save_alarm_toast);
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    private String getStringTime(int minutes, int hour){

        if(minutes < 10) {
            return String.valueOf(hour) + ":0" + String.valueOf(minutes);
        }
        return String.valueOf(hour) + ":" + String.valueOf(minutes);

    }

    private void endIntentToMain(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
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
        edit.commit();
    }

}

