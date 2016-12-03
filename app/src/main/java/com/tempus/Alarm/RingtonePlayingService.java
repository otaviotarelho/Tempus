package com.tempus.Alarm;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.util.Log;
import com.tempus.R;

public class RingtonePlayingService extends Service {
    public MediaPlayer mediaPlayer;
    public boolean isRunning = false;
    private int startId;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String state = intent.getExtras().getString("ALARM_SELECTED");
        String song = intent.getExtras().getString("RINGTONE");
        String time = intent.getExtras().getString("TIME");
        String alarmName = intent.getExtras().getString("ALARM_NAME");
        int id = intent.getExtras().getInt("ALARM_ID");
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        if(state != null){
            switch (state) {
                case "alarm_on":
                    this.startId = 1;
                    break;
                case ("alarm_off"):
                    this.startId = 0;
                    break;
            }
        } else {
            this.startId = 0;
        }


        if(!this.isRunning && this.startId == 1){

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            Intent alarm = new Intent(this.getApplicationContext(), AlarmRingingActivity.class);
            alarm.putExtra("ALARM_SELECTED",state);
            alarm.putExtra("RINGTONE", song);
            alarm.putExtra("TIME", time);
            alarm.putExtra("ALARM_NAME", alarmName);
            alarm.putExtra("ALARM_ID", id);
            PendingIntent pandingAlarm = PendingIntent.getActivity(this, 0, alarm, 0);
            Notification notification = new Notification.Builder(this)
                    .setContentText("Select an action to this alarm")
                    .setPriority(Notification.PRIORITY_MAX)
                    .setSmallIcon(R.drawable.ic_alarm_white_36dp)
                    .setContentTitle("Alarm is ringing")
                    .setContentIntent(pandingAlarm)
                    .setAutoCancel(true)
                    .build();
            notificationManager.notify(0, notification);
            alarm.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            this.startActivity(alarm);

            if(!("").equals(song)){
                Uri ringtone = Uri.parse(song);
                mediaPlayer = MediaPlayer.create(this, ringtone);
                mediaPlayer.setLooping(true);
                mediaPlayer.setVolume(100,100);
                mediaPlayer.start();
            }
            if (vibrator != null)
                vibrator.vibrate(10000);
            this.isRunning = true;
            this.startId = 0;
        }
        else if(this.isRunning && this.startId == 0) {
            if(!("").equals(song)){
                mediaPlayer.stop();
                mediaPlayer.reset();
            }
            vibrator.cancel();
            this.isRunning = false;
            this.startId = 0;
        }
        else if(!this.isRunning && this.startId == 0) {
            this.isRunning = false;
            this.startId = 0;
        }
        else {
            this.isRunning = false;
            this.startId = 1;
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.e("on Destroy called", "");
    }
}
