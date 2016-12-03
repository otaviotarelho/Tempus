/*
 * Copyright (c) 2016. This app was made by Otavio Tarelho and Diego Nunes as requirement to get their major certificate. Any copy of this project will suffer legal penalties under Copyrights Laws.
 */

package com.tempus.Alarm;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.tempus.MainActivity;
import com.tempus.R;
import com.tempus.auxiliars.DatabaseHelper;

import java.util.ArrayList;

public class AlarmFragment extends Fragment {
    private DatabaseHelper tempusDB;
    public  ArrayList<Alarm> alarms = new ArrayList<>();
    private AlarmAdapter adapter;
    private AlertDialog confirmDialogObj;
    private ListView listView;

    public AlarmFragment() {}

    public static AlarmFragment newInstance(){ return new AlarmFragment(); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        setAlarms();
        View fragmentLayout = inflater.inflate(R.layout.fragment_alarm, container, false);
        listView = (ListView) fragmentLayout.findViewById(R.id.listView);
        adapter = new AlarmAdapter(getActivity(), alarms);
        listView.setAdapter(adapter);
        return fragmentLayout;
    }

    @Override
    public void onResume() {
        super.onResume();
        setAlarms();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        registerForContextMenu(listView);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater menuInflater = getActivity().getMenuInflater();
        menuInflater.inflate(R.menu.long_click_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item){
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)
                item.getMenuInfo();
        switch (item.getItemId()){
            case R.id.edit:
                Intent intent = new Intent(getContext(), NewAlarmActivity.class);
                intent.putExtra("DATA", alarms.get(info.position));
                intent.putExtra("ALARM", MainActivity.EXTRA_MESSAGE_EDIT);
                intent.putExtra("POSITION", info.position);
                getContext().startActivity(intent);
                return true;
            case R.id.delete:
                buildConformDialog(alarms.get(info.position).getID());
                confirmDialogObj.show();
                return true;
            case R.id.view:
                if(alarms.get(info.position).getType().equals("0")){
                    buildViewTrafficDialog();
                    confirmDialogObj.show();
                    return true;
                }

                Intent map = new Intent(getContext(), ViewMapActivity.class);
                map.putExtra("LOCATION", alarms.get(info.position).getEvent().getLocation());
                getContext().startActivity(map);
                return true;
        }

        return super.onContextItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putSerializable(MainActivity.SAVE_ALARM_LIST, alarms);
    }

    public void setAlarms(){
        tempusDB = new DatabaseHelper(getActivity());
        alarms = tempusDB.savedAlarms();
    }

    private void buildConformDialog(final long position){
        AlertDialog.Builder confirmBuilder = new AlertDialog.Builder(getActivity());
        confirmBuilder.setTitle(R.string.delete_alarm_title);
        confirmBuilder.setMessage(R.string.delete_alarm_sum);
        confirmBuilder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which){
                tempusDB.deleteAlarm(position);
                alarms.clear();
                alarms = tempusDB.savedAlarms();
                adapter.notifyDataSetChanged();
            }
        });

        confirmBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which){
            }
        });

        confirmDialogObj = confirmBuilder.create();
    }

    private void buildViewTrafficDialog(){
        AlertDialog.Builder confirmBuilder = new AlertDialog.Builder(getActivity());
        confirmBuilder.setTitle(R.string.traffic_view_title_alart);
        confirmBuilder.setMessage(R.string.traffic_view_sum_alart);

        confirmBuilder.setNegativeButton(R.string.ok, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which){

            }
        });

        confirmDialogObj = confirmBuilder.create();
    }
}

