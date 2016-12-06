package com.tempus.AlarmUpdate;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;

import com.tempus.Alarm.Alarm;
import com.tempus.Alarm.AlarmReceiver;
import com.tempus.Alarm.TravelTimeProvider;
import com.tempus.auxiliars.DatabaseHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static android.content.Context.ALARM_SERVICE;

public class AlarmUpdateReceiver extends BroadcastReceiver {

    DatabaseHelper tempusDB;
    String travelTime;

    @Override
    public void onReceive(Context context, Intent intent) {
        Alarm alarm = (Alarm) intent.getExtras().getSerializable("ALARM_INFORMATIONS");
        tempusDB = new DatabaseHelper(context);
        Intent sendIntend = new Intent(context, TravelTimeProvider.class);
        sendIntend.putExtra("EVENT_LOCATION", alarm.getEvent().getLocation());
        ((Activity) context).startActivityForResult(sendIntend, 1);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String newTime = AlarmChangeRules.updateTimeAlarm(alarm.getAlarmTime(), Integer.valueOf(alarm.getAlarmETA()),
                Integer.valueOf(travelTime));

        if(!newTime.equals(alarm.getAlarmTime())){
            if(convertTimeInMili(newTime) <= currentTimeInMili()){
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.MINUTE, 2);
                deletOldAlarmSet(context, alarm);
                AddNewAlarmSet(context, alarm, calendar.getTimeInMillis());
            }
            else{
                deletOldAlarmSet(context, alarm);
                AddNewAlarmSet(context, alarm, convertTimeInMili(newTime));
            }
            mBuilder.setContentTitle("Tempus - Your TimeManager");
            mBuilder.setContentText("Hey, I've checked and updated ("+alarm.getAlarmName()+") to a better time!");
        } else {
            mBuilder.setContentTitle("Tempus - Your TimeManager");
            mBuilder.setContentText("Hey, I've checked and you're alarm ("+alarm.getAlarmName()+") doensn't need a update.");
        }

        mNotificationManager.notify(1, mBuilder.build());
    }

    private String[] getHourMin(String hour){
        return hour.split(":");
    }

    private long convertTimeInMili(String timeString){
        Calendar calendar = Calendar.getInstance();
        String[] time = getHourMin(timeString);
        calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(time[0]));
        calendar.set(Calendar.MINUTE, Integer.valueOf(time[1]));
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTimeInMillis();
    }

    private long currentTimeInMili(){
        Calendar calendar = Calendar.getInstance();
        return calendar.getTimeInMillis();
    }

    private void deletOldAlarmSet(Context context, Alarm a){
        Intent my_intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent;
        final AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        my_intent.putExtra("RINGTONE", a.getRingtone());
        my_intent.putExtra("TIME", a.getAlarmTime());
        my_intent.putExtra("ALARM_NAME", a.getAlarmName());
        my_intent.putExtra("ALARM_ID", a.getID());

        Calendar calendar = Calendar.getInstance();
        String[] time = getHourMin(a.getAlarmTime());
        calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(time[0]));
        calendar.set(Calendar.MINUTE, Integer.valueOf(time[1]));
        calendar.set(Calendar.SECOND, 0);
        final int _id = (int) a.getID();
        pendingIntent = PendingIntent.getBroadcast(context, _id, my_intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);
        context.sendBroadcast(my_intent);
    }

    private void AddNewAlarmSet(Context context, Alarm a, long newTime){
        DateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        Intent my_intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent;
        final AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        my_intent.putExtra("RINGTONE", a.getRingtone());
        my_intent.putExtra("TIME", a.getAlarmTime());
        my_intent.putExtra("ALARM_NAME", a.getAlarmName());
        my_intent.putExtra("ALARM_ID", a.getID());

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(newTime);
        calendar.set(Calendar.SECOND, 0);
        final int _id = (int) a.getID();
        pendingIntent = PendingIntent.getBroadcast(context, _id, my_intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);
        context.sendBroadcast(my_intent);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        tempusDB.updateAlarmETAeTime(a.getID(), dateFormat.format(calendar.getTime()), travelTime);
    }
}
