package com.tempus;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Switch;
import android.widget.TextClock;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by otaviotarelho on 7/30/16.
 */
public class AlarmAdapter extends  ArrayAdapter<Alarm> {


    public AlarmAdapter(Context context, ArrayList<Alarm> alarms) {
        super(context,0,alarms);
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent){

        convertView = LayoutInflater.from(getContext()).inflate(R.layout.alarms_rows, parent, false);

        rowsElements elements = new rowsElements();
        elements.clock = (TextClock) convertView.findViewById(R.id.alarmClock);
        elements.name = (TextView) convertView.findViewById(R.id.ListItemAlarmName);
        elements.eta = (TextView) convertView.findViewById(R.id.ListItemETA);
        elements.active = (Switch) convertView.findViewById(R.id.switch_alarm);


        return convertView;
    }

    public class rowsElements {
        TextClock clock;
        TextView name, eta;
        Switch active;
    }
}
