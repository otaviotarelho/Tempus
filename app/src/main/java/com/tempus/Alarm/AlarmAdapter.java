/*
 * Copyright (c) 2016. This app was made by Otavio Tarelho and Diego Nunes as requirement to get their major certificate. Any copy of this project will suffer legal penalties under Copyrights Laws.
 */

package com.tempus.Alarm;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Switch;
import android.widget.TextClock;
import android.widget.TextView;

import com.tempus.AlarmUpdate.AlarmChangeRules;
import com.tempus.AlarmUpdate.AlarmUpdateReceiver;
import com.tempus.R;
import com.tempus.auxiliars.DatabaseHelper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.ALARM_SERVICE;

class AlarmAdapter extends  ArrayAdapter<Alarm> {
    private DatabaseHelper tempusDB;
    private ArrayList<Alarm> alarm;
    private PendingIntent pendingIntent;
    private PendingIntent pendingIntentAjusteAlarm;
    private static Context context;

    static Context getAppContext(){
        return AlarmAdapter.context;
    }

    AlarmAdapter(Context context, ArrayList<Alarm> alarms) {
        super(context,0,alarms);
        alarm = alarms;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent){
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.alarms_rows, parent, false);
        }
        context = getContext();
        tempusDB = new DatabaseHelper(getContext());
        final AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        final rowsElements elements = new rowsElements();
        elements.clock = (TextClock) convertView.findViewById(R.id.alarmClock);
        elements.name = (TextView) convertView.findViewById(R.id.ListItemAlarmName);
        elements.eta = (TextView) convertView.findViewById(R.id.ListItemETA);
        elements.active = (Switch) convertView.findViewById(R.id.switch_alarm);

        final Alarm a = alarm.get(position);

        if(android.text.format.DateFormat.is24HourFormat(getContext())) {
            elements.clock.setFormat24Hour(a.getAlarmTime());
        } else {
            elements.clock.setFormat12Hour(convert24Hours(a.getAlarmTime()));
        }

        elements.active.setChecked(a.isActive());

        if(a.isActive()) {
            elements.active.setText(R.string.alarm_on);
        } else {
            elements.active.setText(R.string.alarm_off);
        }
        int timeInMinutes = Integer.parseInt(a.getAlarmETA());
        String hours = String.valueOf(timeInMinutes / 60);
        String minutes = String.valueOf(timeInMinutes % 60);
        minutes = minutes.length() == 1 ? "0" + minutes : minutes;
        String tempo = hours + ":" + minutes;
        elements.eta.setText(tempo);
        elements.name.setText(a.getAlarmName());
        elements.active.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent my_intent = new Intent(context, AlarmReceiver.class);
                my_intent.putExtra("RINGTONE", a.getRingtone());
                my_intent.putExtra("TIME", a.getAlarmTime());
                my_intent.putExtra("ALARM_NAME", a.getAlarmName());
                my_intent.putExtra("ALARM_ID", a.getID());

                Intent my_ajust = new Intent(context, AlarmUpdateReceiver.class);
                my_ajust.putExtra("ALARM_INFORMATIONS", alarm);

                if(elements.active.isChecked()){
                    my_intent.putExtra("ALARM_SELECTED", "alarm_on");
                    elements.active.setText(R.string.alarm_on);
                    Calendar calendar = Calendar.getInstance();
                    String[] time = getHourMin(a.getAlarmTime());
                    calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(time[0]));
                    calendar.set(Calendar.MINUTE, Integer.valueOf(time[1]));
                    calendar.set(Calendar.SECOND, 0);
                    final int _id = (int) a.getID();
                    pendingIntent = PendingIntent.getBroadcast(context, _id, my_intent,
                            PendingIntent.FLAG_UPDATE_CURRENT);

                    alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                    tempusDB.updateStatus(a.getID(), true);

                    if("1".equals(a.getType())) {
                        pendingIntentAjusteAlarm = PendingIntent.getBroadcast(context, _id + 9000,
                                my_ajust, PendingIntent.FLAG_UPDATE_CURRENT);
                        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
                        int sync = Integer.valueOf(sharedPref.getString("sync_frequency", ""));
                        Long setTimeUpdate = AlarmChangeRules.updateTimeStartRun(a.getAlarmTime(), sync);
                        Calendar currentTime = Calendar.getInstance();

                        if(setTimeUpdate < currentTime.getTimeInMillis()){
                            setTimeUpdate = currentTime.getTimeInMillis();
                        }
                        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
                        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                        mBuilder.setContentTitle("Tempus - Your TimeManager");
                        mBuilder.setContentText("Hey, I've schedulared a verification to start at " + setTimeUpdate );
                        alarmManager.setRepeating(AlarmManager.RTC, setTimeUpdate, 1000 * 60 * 5, pendingIntentAjusteAlarm);
                    }
                }
                else {
                    my_intent.putExtra("ALARM_SELECTED", "alarm_off");
                    elements.active.setText(R.string.alarm_off);
                    alarmManager.cancel(pendingIntent);
                    context.sendBroadcast(my_intent);
                    if("1".equals(a.getType())) {
                        alarmManager.cancel(pendingIntentAjusteAlarm);
                        context.sendBroadcast(my_ajust);
                    }
                    tempusDB.updateStatus(a.getID(), false);
                }
            }

        });

        return convertView;
    }

    private String[] getHourMin(String hour){
        return hour.split(":");
    }

    private String convert24Hours(String hour){
        final DateFormat sdf;
        final Date dateObj;

        try {
            DateFormat displayFormat = new SimpleDateFormat("HH:mm aa", Locale.getDefault());
            sdf = DateFormat.getTimeInstance(DateFormat.SHORT);
            dateObj = displayFormat.parse(hour);
            return sdf.format(dateObj);
        } catch (final ParseException e) {
            e.printStackTrace();
        }

        return "";
    }


    private class rowsElements {
        TextClock clock;
        TextView name, eta;
        Switch active;
    }
}
