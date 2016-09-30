/*
 * Copyright (c) 2016. This app was made by Otavio Tarelho and Diego Nunes as requirement to get their major certificate. Any copy of this project will suffer legal penalties under Copyrights Laws.
 */

package com.tempus.Alarm;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Switch;
import android.widget.TextClock;
import android.widget.TextView;
import com.tempus.R;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class AlarmAdapter extends  ArrayAdapter<Alarm> {

    private ArrayList<Alarm> alarm;

    public AlarmAdapter(Context context, ArrayList<Alarm> alarms) {

        super(context,0,alarms);
        alarm = alarms;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent){

        convertView = LayoutInflater.from(getContext()).inflate(R.layout.alarms_rows, parent, false);

        final rowsElements elements = new rowsElements();
        elements.clock = (TextClock) convertView.findViewById(R.id.alarmClock);
        elements.name = (TextView) convertView.findViewById(R.id.ListItemAlarmName);
        elements.eta = (TextView) convertView.findViewById(R.id.ListItemETA);
        elements.active = (Switch) convertView.findViewById(R.id.switch_alarm);

        Alarm a = alarm.get(position);

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
                if(elements.active.isChecked()){

                    elements.active.setText(R.string.alarm_on);
                    //turn AlarBroadCast On

                }
                else {

                    elements.active.setText(R.string.alarm_off);
                    //turn AlarmBroadCast off;

                }
            }

        });

        return convertView;
    }

    private String convert24Hours(String hour){
        final DateFormat sdf;
        final Date dateObj;

        try {
            DateFormat displayFormat = new SimpleDateFormat("HH:mm");
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
