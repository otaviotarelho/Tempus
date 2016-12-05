package com.tempus.Alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextClock;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.tempus.AlarmUpdate.AlarmUpdateReceiver;
import com.tempus.MainActivity;
import com.tempus.R;
import com.tempus.auxiliars.DatabaseHelper;

public class AlarmRingingActivity extends AppCompatActivity {
    private FirebaseAnalytics mFirebaseAnalytics;
    private DatabaseHelper tempusDB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_ringing);

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        tempusDB = new DatabaseHelper(this);
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        Button btn_off = (Button) findViewById(R.id.turn_off);
        Intent intent = getIntent();
        final String song = intent.getExtras().getString("RINGTONE");
        final String time = intent.getExtras().getString("TIME");
        final String alarm = intent.getExtras().getString("ALARM_NAME");
        final int id = intent.getExtras().getInt("ALARM_ID");
        final Context context = this;

        TextClock textClock = (TextClock) findViewById(R.id.alarmClockRing);
        TextView textView = (TextView) findViewById(R.id.alarmName);
        textClock.setFormat24Hour(time);
        textView.setText(alarm);

        btn_off.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent my_intent = new Intent(AlarmAdapter.getAppContext(), AlarmReceiver.class);
                Intent my_Ajust = new Intent(AlarmAdapter.getAppContext(), AlarmUpdateReceiver.class);
                my_intent.putExtra("RINGTONE", song);
                my_intent.putExtra("TIME", time);
                my_intent.putExtra("ALARM_SELECTED", "alarm_off");
                my_intent.putExtra("ALARM_NAME", alarm);
                my_intent.putExtra("ALARM_ID", id);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(AlarmAdapter.getAppContext(), id, my_intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                PendingIntent pendingIntentAjuste = PendingIntent.getBroadcast(AlarmAdapter.getAppContext(), id + 9000, my_Ajust,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager alarmManager = (AlarmManager) AlarmAdapter.getAppContext().getSystemService(ALARM_SERVICE);
                alarmManager.cancel(pendingIntentAjuste);
                context.sendBroadcast(my_Ajust);
                alarmManager.cancel(pendingIntent);
                context.sendBroadcast(my_intent);
                tempusDB.updateStatus((long) id, false);
                Intent main = new Intent(context, MainActivity.class);
                startActivity(main);
            }
        });
    }
}
