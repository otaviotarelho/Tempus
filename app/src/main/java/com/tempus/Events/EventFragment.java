/*
 * Copyright (c) 2016. This app was made by Otavio Tarelho and Diego Nunes as requirement to get their major certificate. Any copy of this project will suffer legal penalties under Copyrights Laws.
 */

package com.tempus.Events;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.tempus.R;
import java.util.ArrayList;
import java.util.Calendar;

public class EventFragment extends Fragment {

    private static ArrayList<Event> events = new ArrayList<>();
    private Context context;
    private static final String[] EVENT_PROJECTION = new String[] {
            CalendarContract.Events.TITLE,
            CalendarContract.Events.EVENT_LOCATION,
            CalendarContract.Events.DTSTART,
            CalendarContract.Events.DTEND,
            CalendarContract.Events.DURATION,
            CalendarContract.Events.ALL_DAY
    };
    private static final int PROJECTION_TITLE = 0;
    private static final int PROJECTION_LOCATION = 1;
    private static final int PROJECTION_DTSTART = 2;
    private static final int PROJECTION_DTEND = 3;
    private static final int PROJECTION_DURATION = 4;
    private static final int ALL_DAY = 5;
    public EventFragment() {}

    public static EventFragment newInstance(){ return new EventFragment(); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View fragmentLayout = inflater.inflate(R.layout.fragment_event, container, false);
        context = getActivity();
        ListView listViewEvent;
        listViewEvent = (ListView) fragmentLayout.findViewById(R.id.listViewsEvents);
        events.clear();
        getEvents();
        listViewEvent.setAdapter(new EventAdapter(getActivity(), events));
        return fragmentLayout;
    }

    @Override
    public void onResume() {
        super.onResume();
        events.clear();
        getEvents();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
    }

    public void getEvents(){

        if(ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) !=
                PackageManager.PERMISSION_DENIED) {
            Cursor cur;
            ContentResolver cr = context.getContentResolver();
            Uri uri = CalendarContract.Events.CONTENT_URI;
            Calendar calendar = Calendar.getInstance();
            long startDay = calendar.getTimeInMillis();
            calendar.add(Calendar.DATE, 6);
            long endDay = calendar.getTimeInMillis();

            StringBuilder selection = new StringBuilder();
            selection.append(CalendarContract.Events.DTSTART)
                    .append(" >= ? AND ")
                    .append(CalendarContract.Events.DTSTART)
                    .append(" <= ? ");

            StringBuilder sortOrder = new StringBuilder();
            sortOrder.append(CalendarContract.Events.DTSTART)
                     .append(" ASC ");

            String[] selectionArgs = new String[] { Long.toString(startDay), Long.toString(endDay) };

            cur = cr.query(uri, EVENT_PROJECTION, selection.toString(), selectionArgs, sortOrder.toString());
            addEventsToArrayList(cur);
        }

    }

    private void addEventsToArrayList(Cursor cur){

        while(cur.moveToNext()){

            Event event = new Event();

            event.setName(cur.getString(PROJECTION_TITLE));

            event.setDay_end(cur.getString(PROJECTION_DTEND));
            event.setLocation(cur.getString(PROJECTION_LOCATION));
            event.setDuration(cur.getString(PROJECTION_DURATION));

            if(cur.getString(ALL_DAY).equals("1")) {
                Calendar cal = Calendar.getInstance();
                Long val = Long.parseLong(cur.getString(PROJECTION_DTSTART), 10);
                cal.setTimeInMillis(val);
                cal.add(Calendar.DATE, 1);
                event.setDay_start(String.valueOf(cal.getTimeInMillis()));
            }else {
                event.setDay_start(cur.getString(PROJECTION_DTSTART));
            }

            events.add(event);
        }

        cur.close();
    }


}
