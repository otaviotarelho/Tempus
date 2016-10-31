package com.tempus.Alarm;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.util.Log;

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
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        switch (state) {
            case "alarm_on":
                this.startId = 1;
                break;
            case ("alarm_off"):
                this.startId = 0;
                break;
            default:
                this.startId = 0;
                break;
        }

        if(!this.isRunning && this.startId == 1){
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
        else if(this.isRunning && this.startId == 1) {
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
