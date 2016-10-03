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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class NewAlarmActivity extends AppCompatPreferenceActivity {

    private AlertDialog confirmDialogObj;
    private Alarm a;
    private Event e;
    private String come_from;
    private int positionArray;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_alarm);
        getFragmentManager().beginTransaction().replace(R.id.preferences_alarm,
                new NewAlarmItems()).commit();

        Bundle extras = getIntent().getExtras();
        come_from = extras.getString("ALARM");

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit = sharedPref.edit();
        ClearPreferences(edit);
        TimePicker textClock = (TimePicker) findViewById(R.id.timePicker);

        if(android.text.format.DateFormat.is24HourFormat(this)){
            textClock.setIs24HourView(true);
        }
        else{
            textClock.setIs24HourView(false);
        }

        if(come_from.equals(MainActivity.EXTRA_MESSAGE_EDIT)) {
            Intent i = getIntent();
            Alarm alarm = (Alarm) i.getSerializableExtra("DATA");
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);

            settings.edit().putString("alarm_name", alarm.getAlarmName()).apply();
            settings.edit().putStringSet("alarm_repeat", alarm.getRepeat()).apply();
            settings.edit().putString("alarm_ringtone", alarm.getRingtone()).apply();
            settings.edit().putBoolean("alarm_snooze", alarm.isSnooze()).apply();
            settings.edit().putString("alarm_type", alarm.getType()).apply();

            if(settings.getString("alarm_type", "").equals("1")){
                settings.edit().putLong("event_start_time",
                        Long.getLong(alarm.getEvent().getDay_start(), 0)).apply();
                settings.edit().putLong("event_end_time",
                        Long.getLong(alarm.getEvent().getDay_end(), 0)).apply();
                settings.edit().putString("event_location", alarm.getEvent().getLocation()).apply();
            }

            positionArray = i.getIntExtra("POSITION", 0); // get array position
            int[] hour = changeTime(alarm.getAlarmTime());
            textClock.setCurrentHour(hour[0]);
            textClock.setCurrentMinute(hour[1]);

        }
        else if(come_from.equals(MainActivity.EXTRA_MESSAGE_ADD_EVENT)){
            Intent i = getIntent();
            e = (Event) i.getSerializableExtra("DATA");

            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
            HashSet<String> to_solve = new HashSet<>();
            to_solve.add("-1");

            settings.edit().putString("alarm_name", e.getName()).apply();
            settings.edit().putStringSet("alarm_repeat", to_solve).apply();
            settings.edit().putString("alarm_ringtone", "").apply();
            settings.edit().putBoolean("alarm_snooze", true).apply();
            settings.edit().putString("alarm_type", "1").apply();

            settings.edit().putLong("event_start_time", Long.getLong(e.getDay_start(), 0)).apply();
            settings.edit().putLong("event_end_time", Long.getLong(e.getDay_end(), 0)).apply();
            settings.edit().putString("event_location", e.getLocation()).apply();

            //textClock.setCurrentHour(Integer.getInteger(getStringFromDate("H", e.getDay_start())));
//            textClock.setCurrentMinute(Integer.getInteger(getStringFromDate("mm", e.getDay_start())));

        }
    }

    @SuppressWarnings("deprecation")
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

    @SuppressWarnings("deprecation")
    public void saveData() {
        //saveData in the database
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        HashSet<String> to_solve = new HashSet<>();

        TimePicker alarmTime = (TimePicker) findViewById(R.id.timePicker);
        String time;
        time = getStringTime(alarmTime.getCurrentMinute(), alarmTime.getCurrentHour()); // HOUR SAVED IN 24

        String title = settings.getString("alarm_name", "");
        Set<String> repeat = settings.getStringSet("alarm_repeat", to_solve);
        String ringtone = settings.getString("alarm_ringtone", "");
        Boolean snooze = settings.getBoolean("alarm_snooze", true);
        String type = settings.getString("alarm_type", "");

        String time_event = String.valueOf(settings.getLong("event_start_time", 0));
        String time_event_end = String.valueOf(settings.getLong("event_end_time", 0));
        String event_location = settings.getString("event_location", "");

        //Hardcoded ALARMS
        if(type.equals("0")) {
            // in case new normal alarm change
            e = new Event();
            a = new Alarm(title, getResources().getString(R.string.normal_alarm),
                    time, ringtone,repeat,type,snooze, true, e);
        }
        else if (type.equals("1")) {
            //in case new alarm from menu and traffic based
            Event ev = new Event();
            ev.setDay_start(time_event);
            ev.setDay_end(time_event_end);
            ev.setLocation(event_location);
            ev.setName(title);

            if(come_from.equals(MainActivity.EXTRA_MESSAGE_EDIT)){
                ev.setDuration(e.getDuration());
            }else{
                ev.setDuration("");
            }

            a = new Alarm(title, ExpectedTimeOfArrivel(event_location)
                    + getResources().getString(R.string.hour)
                    , time, ringtone,repeat,type,snooze, true, ev);
        }


        if(come_from.equals(MainActivity.EXTRA_MESSAGE) ||
                come_from.equals(MainActivity.EXTRA_MESSAGE_ADD_EVENT)){
            AlarmFragment.alarms.add(a);

        }
        else if(come_from.equals(MainActivity.EXTRA_MESSAGE_EDIT)) {
            //in case new alarm from events tab
            AlarmFragment.alarms.remove(positionArray);
            AlarmFragment.alarms.add(a);
        }

        // return to home
        endIntentToMain();
        //Set toast mensage
        toastMessage();
        //Finish this activity
        finish();
    }

    private int ExpectedTimeOfArrivel(String event_location) {
        //fazer o maps here
        return 122;
    }

    @SuppressWarnings("deprecation")
    private int[] changeTime(String hour){
        final Date dateObj;
        int[] completeHour = new int[2];

        try {
            SimpleDateFormat displayFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            dateObj = displayFormat.parse(hour);
            completeHour[0] = dateObj.getHours();
            completeHour[1] = dateObj.getMinutes();
        } catch (final ParseException e) {
            e.printStackTrace();
        }

        return completeHour;
    }

    public String getStringFromDate(String pattern, String longDTSTART){
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.getDefault());
        Calendar c = Calendar.getInstance();
        Long mili = Long.valueOf(longDTSTART);
        c.setTimeInMillis(mili);
        return sdf.format(c.getTime());
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

