package com.tempus.Alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextClock;
import android.widget.TextView;

import com.tempus.MainActivity;
import com.tempus.R;

public class AlarmRingingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_ringing);
        getActionBar().setTitle("");
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        Button btn_off = (Button) findViewById(R.id.turn_off);
        Intent intent = getIntent();
        final String state = intent.getExtras().getString("ALARM_SELECTED");
        final String song = intent.getExtras().getString("RINGTONE");
        final String time = intent.getExtras().getString("TIME");
        final String alarm = intent.getExtras().getString("ALARM_NAME");
        final Context context = this;

        TextClock textClock = (TextClock) findViewById(R.id.alarmClockRing);
        TextView textView = (TextView) findViewById(R.id.alarmName);
        textClock.setFormat24Hour(time);
        textView.setText(alarm);

        btn_off.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent my_intent = new Intent(AlarmAdapter.getAppContext(), AlarmReceiver.class);
                my_intent.putExtra("RINGTONE", song);
                my_intent.putExtra("TIME", time);
                my_intent.putExtra("ALARM_SELECTED", state);
                my_intent.putExtra("ALARM_NAME", alarm);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(AlarmAdapter.getAppContext(), 0, my_intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager alarmManager = (AlarmManager) AlarmAdapter.getAppContext().getSystemService(ALARM_SERVICE);
                alarmManager.cancel(pendingIntent);
                Intent main = new Intent(context, MainActivity.class);
                startActivity(main);
            }
        });
    }
}
