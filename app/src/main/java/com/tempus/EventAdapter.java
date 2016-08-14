/*
 * Copyright (c) 2016. This app was made by Otavio Tarelho and Diego Nunes as requirement to get their major certificate. Any copy of this project will suffer legal penalties under Copyrights Laws.
 */

package com.tempus;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextClock;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by otaviotarelho on 7/31/16.
 */

public class EventAdapter extends ArrayAdapter<Event> {
    private ArrayList<Event> e;
    public EventAdapter(Context context, ArrayList<Event> events) {
        super(context,0,events);
        e = events;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){

        convertView = LayoutInflater.from(getContext()).inflate(R.layout.events_rows, parent, false);

        ErowsElements elements = new ErowsElements();

        elements.name = (TextView) convertView.findViewById(R.id.EventNameList);
        elements.date = (TextView) convertView.findViewById(R.id.EventDateList);
        elements.time = (TextClock) convertView.findViewById(R.id.EventTimeClock);

        Event ev;
        ev = e.get(position);

        elements.name.setText(ev.getName());
        elements.date.setText(ev.getDay_start());
        elements.time.setText(ev.getTime_start());

        return convertView;
    }

    public class ErowsElements {
        TextView name, date;
        TextClock time;
    }


}
