package com.tempus;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventFragment extends Fragment {

    private static ArrayList<Event> events = new ArrayList<>();
    private static ListView listViewEvent;

    public EventFragment() {
        // Required empty public constructor
    }

    public static EventFragment newInstance(){ return new EventFragment(); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View fragmentLayout = inflater.inflate(R.layout.fragment_event, container, false);

        setEvents();

        listViewEvent = (ListView) fragmentLayout.findViewById(R.id.listViewsEvents);
        listViewEvent.setAdapter(new EventAdapter(getActivity(),events));

        // Inflate the layout for this fragment
        return fragmentLayout;
    }

    public static void setEvents(){

        events.add(new Event("Event1", "12345677.34-1993005","","","","",1));
        events.add(new Event("Event2", "12345677.34-1993005","","","","",1));

    }
}
