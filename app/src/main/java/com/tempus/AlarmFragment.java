/*
 * Copyright (c) 2016. This app was made by Otavio Tarelho and Diego Nunes as requirement to get their major certificate. Any copy of this project will suffer legal penalties under Copyrights Laws.
 */

package com.tempus;

/* Imports section */
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import java.util.ArrayList;

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

        //If to prevent add same items to the ArrayList
        if(savedInstanceState != null) {

            alarms = (ArrayList<Alarm>) savedInstanceState.get(MainActivity.SAVE_ALARM_LIST);

        }
        else {

            if(alarms.size() == 0) {
                setAlarms();
            }

        }

        listView = (ListView) fragmentLayout.findViewById(R.id.listViews);
        listView.setAdapter(new AlarmAdapter(getActivity(), alarms));
        return fragmentLayout;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putSerializable(MainActivity.SAVE_ALARM_LIST, alarms);
    }



    public static void setAlarms(){
        alarms.add(new Alarm("Primeiro alarm", "2 horas","10:30", false));
        alarms.add(new Alarm("Segundo alarm", "2 horas","", true));
    }
}

