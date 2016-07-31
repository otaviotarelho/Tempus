package com.tempus;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class AlarmFragment extends Fragment {

    private static ArrayList<Alarm> alarms = new ArrayList<>();
    private static ListView listView;

    public AlarmFragment() {
        // Required empty public constructor
    }

    public static AlarmFragment newInstance(){ return new AlarmFragment(); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentLayout = inflater.inflate(R.layout.fragment_alarm, container, false);

        setAlarms();

        listView = (ListView) fragmentLayout.findViewById(R.id.listViews);
        listView.setAdapter(new AlarmAdapter(getActivity(),alarms));

        return fragmentLayout;
    }

    public static void setAlarms(){
        alarms.add(new Alarm("Primeiro alarm", "2 horas","",true));
        alarms.add(new Alarm("Segundo alarm", "2 horas","",true));
    }
}
