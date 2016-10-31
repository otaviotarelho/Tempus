/*
 * Copyright (c) 2016. This app was made by Otavio Tarelho and Diego Nunes as requirement to get their major certificate. Any copy of this project will suffer legal penalties under Copyrights Laws.
 */

package com.tempus.Alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Switch;
import android.widget.TextClock;
import android.widget.TextView;
import com.tempus.R;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.ALARM_SERVICE;

public class AlarmAdapter extends  ArrayAdapter<Alarm> {

    private ArrayList<Alarm> alarm;
    private Context context;
    private PendingIntent pendingIntent;

    public AlarmAdapter(Context context, ArrayList<Alarm> alarms) {
        super(context,0,alarms);
        alarm = alarms;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        convertView = LayoutInflater.from(getContext()).inflate(R.layout.alarms_rows, parent, false);
        context = getContext();
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

        elements.eta.setText(a.getAlarmETA());
        elements.name.setText(a.getAlarmName());
        elements.active.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent my_intent = new Intent(context, AlarmReceiver.class);
                my_intent.putExtra("RINGTONE", a.getRingtone());

                if(elements.active.isChecked()){
                    my_intent.putExtra("ALARM_SELECTED", "alarm_on");
                    elements.active.setText(R.string.alarm_on);
                    Calendar calendar = Calendar.getInstance();
                    String[] time = getHourMin(a.getAlarmTime());
                    calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(time[0]));
                    calendar.set(Calendar.MINUTE, Integer.valueOf(time[1]));
                    calendar.set(Calendar.SECOND, 0);

                    pendingIntent = PendingIntent.getBroadcast(context, 0, my_intent,
                            PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                }
                else {
                    my_intent.putExtra("ALARM_SELECTED", "alarm_off");
                    elements.active.setText(R.string.alarm_off);
                    alarmManager.cancel(pendingIntent);
                    context.sendBroadcast(my_intent);
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
            DateFormat displayFormat = new SimpleDateFormat("HH:mm", Locale.UK);
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
