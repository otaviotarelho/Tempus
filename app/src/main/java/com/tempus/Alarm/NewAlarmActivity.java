/*
 * Copyright (c) 2016. This app was made by Otavio Tarelho and Diego Nunes as requirement to get their major certificate. Any copy of this project will suffer legal penalties under Copyrights Laws.
 */

package com.tempus.Alarm;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TimePicker;

import com.tempus.Events.Event;
import com.tempus.MainActivity;
import com.tempus.Preferences.AppCompatPreferenceActivity;
import com.tempus.R;
import com.tempus.auxiliars.DatabaseHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;

public class NewAlarmActivity extends AppCompatPreferenceActivity implements TravelTimeProvider.TravelTimeCallback {

    private DatabaseHelper tempusDB;
    private AlertDialog confirmDialogObj;
    private Alarm a;
    private Event e;
    private String come_from;
    private long id;
    public static String locationPicked;
    private Boolean saved = false;
    private TravelTimeProvider mTravelTimeProvider;
    private String travelTime = null;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_alarm);
        getFragmentManager().beginTransaction().replace(R.id.preferences_alarm,
                new NewAlarmItems()).commit();

        Bundle extras = getIntent().getExtras();
        come_from = extras.getString("ALARM");
        tempusDB = new DatabaseHelper(this);
        this.setTitle(R.string.new_alarm);

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
            this.setTitle(R.string.edit_alarm);
            Alarm alarm = (Alarm) i.getSerializableExtra("DATA");
            id = alarm.getID();
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
            settings.edit().putString("alarm_name", alarm.getAlarmName()).apply();
            //settings.edit().putStringSet("alarm_repeat", alarm.getRepeat()).apply();
            settings.edit().putString("alarm_ringtone", alarm.getRingtone()).apply();
            //settings.edit().putBoolean("alarm_snooze", alarm.isSnooze()).apply();
            settings.edit().putString("alarm_type", alarm.getType()).apply();

            if(settings.getString("alarm_type", "").equals("1")){
                settings.edit().putLong("event_start_time",
                        Long.parseLong(alarm.getEvent().getDay_start(), 10)).apply();
                settings.edit().putLong("event_end_time",
                        Long.parseLong(alarm.getEvent().getDay_end(), 10)).apply();
                settings.edit().putString("event_location", alarm.getEvent().getLocation()).apply();
            }
            int[] hour = changeTime(alarm.getAlarmTime());
            textClock.setCurrentHour(hour[0]);
            textClock.setCurrentMinute(hour[1]);

        }
        else if(come_from.equals(MainActivity.EXTRA_MESSAGE_ADD_EVENT)){
            Intent i = getIntent();
            e = (Event) i.getSerializableExtra("DATA");

            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
            // HashSet<String> to_solve = new HashSet<>();
            // to_solve.add("-1");
            settings.edit().putString("alarm_name", e.getName()).apply();
            //settings.edit().putStringSet("alarm_repeat", to_solve).apply();
            settings.edit().putString("alarm_ringtone", "").apply();
            //settings.edit().putBoolean("alarm_snooze", true).apply();
            settings.edit().putString("alarm_type", "1").apply();

            settings.edit().putLong("event_start_time", Long.parseLong(e.getDay_start(), 10)).apply();
            settings.edit().putLong("event_end_time", Long.parseLong(e.getDay_end(), 10)).apply();
            settings.edit().putString("event_location", e.getLocation()).apply();

            textClock.setCurrentHour(Integer.valueOf(getStringFromDate("H", e.getDay_start())));
            textClock.setCurrentMinute(Integer.valueOf(getStringFromDate("mm", e.getDay_start())));

        }
        mTravelTimeProvider = new TravelTimeProvider(this, this, null);

    }

    @SuppressWarnings("deprecation")
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
        mTravelTimeProvider.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mTravelTimeProvider.disconnect();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
    }

    @SuppressWarnings("deprecation")
    public void saveData() {
        int error_motivo = 0;
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        //HashSet<String> to_solve = new HashSet<>();
        TimePicker alarmTime = (TimePicker) findViewById(R.id.timePicker);
        String time;
        time = getStringTime(alarmTime.getCurrentMinute(), alarmTime.getCurrentHour()); // HOUR SAVED IN 24
        String title = settings.getString("alarm_name", "");
        //Set<String> repeat = settings.getStringSet("alarm_repeat", to_solve);
        String ringtone = settings.getString("alarm_ringtone", "");
        //Boolean snooze = settings.getBoolean("alarm_snooze", true);
        String type = settings.getString("alarm_type", "");

        String time_event = String.valueOf(settings.getLong("event_start_time", 0));
        String time_event_end = String.valueOf(settings.getLong("event_end_time", 0));
        String event_location = locationPicked;
        Log.e("SAVING DATA", "TRUE");
        if(type.equals("0")) {
            e = new Event();
            a = new Alarm(title, getResources().getString(R.string.normal_alarm),
                    time, ringtone,/*repeat,*/type,/*snooze,*/ false, e);
            if("".equals(ringtone)) {
                buildConformDialogAlertSilentAlarm();
                confirmDialogObj.show();
            } else {
                saved = true;
            }
        }
        else if (type.equals("1")) {

            if(time_event.equals(time_event_end) ||
                    getCurrentHourFromLong(time_event) > getCurrentHourFromLong(time_event_end)) {
                saved = false;
                error_motivo = 1;
            }
            else if("-1".equals(expectedTimeOfArrivel(event_location))){
                error_motivo = 2;
                saved = false;
            }
            else if("".equals(event_location)){
                error_motivo = 3;
                saved = false;
            }
            else {
                if("".equals(ringtone)){
                    buildConformDialogAlertSilentAlarm();
                    confirmDialogObj.show();
                } else{
                    saved = true;
                }

                Event ev = new Event();
                ev.setDay_start(time_event);
                ev.setDay_end(time_event_end);
                ev.setLocation(event_location);
                ev.setName(title);

                if(come_from.equals(MainActivity.EXTRA_MESSAGE_EDIT)) {
                    ev.setDuration(e.getDuration());
                }
                else {
                    ev.setDuration("");
                }

                a = new Alarm(title, expectedTimeOfArrivel(event_location)
                        , time, ringtone,/*repeat,*/type,/*snooze,*/ false, ev);

            }
        }

        if(saved){
            endIntentToMain();
            finish();
            if(come_from.equals(MainActivity.EXTRA_MESSAGE) ||
                    come_from.equals(MainActivity.EXTRA_MESSAGE_ADD_EVENT)){
                tempusDB.insertAlarm(a);

            }
            else if(come_from.equals(MainActivity.EXTRA_MESSAGE_EDIT)) {
                a.setID(id);
                tempusDB.updateAlarm(a);
            }
        } else {
            buildDialogError(error_motivo);
        }
    }

    private void buildDialogError(int motivo){
        switch (motivo){
            case 1:
                buildConformDialogError();
                confirmDialogObj.show();
                break;
            case 2:
                buildConformDialogErrorMaps();
                confirmDialogObj.show();
                break;
            case 3:
                buildConformDialogErrorPlace();
                break;
        }
    }

    private String expectedTimeOfArrivel(String event_location) {
        //fazer o maps here
        mTravelTimeProvider.connect();

        mTravelTimeProvider = new TravelTimeProvider(this, this, event_location);

        if(travelTime == null){
            return "-1";
        }
        Log.d("tempo de duração: ", travelTime);
        return travelTime;
    }

    @SuppressWarnings("deprecation")
    private int getCurrentHourFromLong(String hour){
        Calendar c = Calendar.getInstance();
        Long h = Long.parseLong(hour);
        c.setTimeInMillis(h);
        Date d = c.getTime();
        return d.getHours();
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

    private void buildConformDialogError(){
        AlertDialog.Builder confirmBuilder = new AlertDialog.Builder(this);
        confirmBuilder.setTitle(R.string.error);
        confirmBuilder.setMessage(R.string.same_time);
        confirmBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which){
            }
        });
        confirmDialogObj = confirmBuilder.create();
    }

    private void buildConformDialogErrorMaps(){
        AlertDialog.Builder confirmBuilder = new AlertDialog.Builder(this);
        confirmBuilder.setTitle(R.string.error);
        confirmBuilder.setMessage(R.string.error_maps);
        confirmBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which){
            }
        });
        confirmDialogObj = confirmBuilder.create();
    }

    private void buildConformDialogErrorPlace(){
        AlertDialog.Builder confirmBuilder = new AlertDialog.Builder(this);
        confirmBuilder.setTitle(R.string.error);
        confirmBuilder.setMessage(R.string.error_place);
        confirmBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which){
            }
        });
        confirmDialogObj = confirmBuilder.create();
    }

    private void buildConformDialogAlertSilentAlarm(){
        AlertDialog.Builder confirmBuilder = new AlertDialog.Builder(this);
        confirmBuilder.setTitle(R.string.error);
        confirmBuilder.setMessage(R.string.error_ringtone);
        confirmBuilder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which){
                saved = true;
            }
        });
        confirmBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which){
                saved = false;
            }
        });
        confirmDialogObj = confirmBuilder.create();
    }

    public void ClearPreferences(SharedPreferences.Editor edit){
        edit.remove("alarm_name");
        edit.remove("alarm_ringtone");
        edit.remove("alarm_type");
        //edit.remove("alarm_snooze");
        //edit.remove("alarm_repeat");
        edit.remove("event_location");
        edit.commit();
    }

    @Override
    public void handleNewTravelTime(String duration) {
        this.travelTime = duration;
    }
}

