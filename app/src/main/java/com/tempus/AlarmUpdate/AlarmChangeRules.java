package com.tempus.AlarmUpdate;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AlarmChangeRules {

    public static String updateTimeNewAlarm(String timeStart, int timeLeave, int timeTraffic) {
         try{
            DateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Calendar dataStartAlarm = Calendar.getInstance();
            Date data = dateFormat.parse(timeStart);
            dataStartAlarm.setTime(data);
            dataStartAlarm.add(Calendar.MINUTE, getTimeLeaveMinusTraffic(timeLeave, timeTraffic));
            return dateFormat.format(dataStartAlarm.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeStart;
    }

    private static int getTimeLeaveMinusTraffic(int timeLeave, int timeTraffic) {
        return -(timeLeave + timeTraffic);
    }

    static String updateTimeAlarm(String timeStart, int OldETA, int NewETA) {
        try {
            DateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Calendar dataStartAlarm = Calendar.getInstance();
            Date data = dateFormat.parse(timeStart);
            dataStartAlarm.setTime(data);

            if(OldETA > NewETA){
                dataStartAlarm.add(Calendar.MINUTE, (OldETA - NewETA) * -1);
            }
            else{
                dataStartAlarm.add(Calendar.MINUTE, (OldETA - NewETA));
            }

            return dateFormat.format(dataStartAlarm.getTime());
        }
        catch (ParseException e) {
            e.printStackTrace();
        }

        return timeStart;
    }

    public static long updateTimeStartRun(String timeStart, int SyncTime){
        try{
            DateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Calendar dataStartAlarm = Calendar.getInstance();
            Calendar dataCurrent = Calendar.getInstance();
            dataCurrent.setTimeInMillis(System.currentTimeMillis());
            Date data = dateFormat.parse(timeStart);
            dataStartAlarm.setTime(data);
            dataStartAlarm.add(Calendar.MINUTE, SyncTime * -1);
            if(dataCurrent.getTimeInMillis() > dataStartAlarm.getTimeInMillis()){
                dataStartAlarm.add(Calendar.DATE, 1);
            }
            return dataStartAlarm.getTimeInMillis();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return 0;
    }

}
