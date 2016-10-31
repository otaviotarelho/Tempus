package com.tempus.Alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String get_type = intent.getExtras().getString("ALARM_SELECTED");
        String ringtone = intent.getExtras().getString("RINGTONE");
        Intent serviceIntent = new Intent(context, RingtonePlayingService.class);
        serviceIntent.putExtra("ALARM_SELECTED", get_type);
        serviceIntent.putExtra("RINGTONE", ringtone);
        context.startService(serviceIntent);
    }
}
