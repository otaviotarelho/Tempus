/*
 * Copyright (c) 2016. This app was made by Otavio Tarelho and Diego Nunes as requirement to get their major certificate. Any copy of this project will suffer legal penalties under Copyrights Laws.
 */

package com.tempus;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextClock;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by otaviotarelho on 7/31/16.
 */

public class EventAdapter extends ArrayAdapter<Event> {
    private ArrayList<Event> e;
    private Context context;

    public EventAdapter(Context context, ArrayList<Event> events) {
        super(context,0,events);
        this.context = context;
        e = events;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){

        convertView = LayoutInflater.from(getContext()).inflate(R.layout.events_rows, parent, false);

        ErowsElements elements = new ErowsElements();

        elements.name = (TextView) convertView.findViewById(R.id.EventNameList);
        elements.date = (TextView) convertView.findViewById(R.id.EventDateList);
        elements.time = (TextClock) convertView.findViewById(R.id.EventTimeClock);
        elements.btn = (ImageButton) convertView.findViewById(R.id.eventAddButton);

        Event ev;
        ev = e.get(position);

        elements.name.setText(ev.getName());
        elements.date.setText(ev.getDay_start());
        elements.time.setText("");

        elements.btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, NewAlarmActivity.class);

                //send informations to new activity about the event


                //start activity
                context.startActivity(intent);
            }
        });

        return convertView;
    }

    public class ErowsElements {
        TextView name, date;
        TextClock time;
        ImageButton btn;
    }


}
