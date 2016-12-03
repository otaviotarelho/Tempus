package com.tempus.auxiliars;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.tempus.Alarm.Alarm;
import com.tempus.Events.Event;

import java.util.ArrayList;
import java.util.List;


public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "tempus.db";
    private static final int VERSION = 2;
    private static final String TABLE_NAME = "tempus_alarm";
    private static final String[] COLUMNS = {
            "id",
            "name",
            "time",
            "ringtoneAddress",
            "type",
            "eta",
            "active",
            "location_ll",
            "day_start",
            "day_end",
            "duration",
    };

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        StringBuilder query = new StringBuilder();
        query.append("CREATE TABLE ")
                .append(TABLE_NAME)
                .append(" ( ")
                .append(COLUMNS[0] + " INTEGER PRIMARY KEY AUTOINCREMENT, ")
                .append(COLUMNS[1] + " TEXT, ")
                .append(COLUMNS[2] + " TEXT, ")
                .append(COLUMNS[3] + " TEXT, ")
                .append(COLUMNS[4] + " TEXT, ")
                .append(COLUMNS[5] + " TEXT, ")
                .append(COLUMNS[6] + " TEXT, ")
                .append(COLUMNS[7] + " INTEGER, ")
                .append(COLUMNS[8] + " TEXT, ")
                .append(COLUMNS[9] + " TEXT, ")
                .append(COLUMNS[10] + " TEXT ")
                .append(" )");
        db.execSQL(query.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

        StringBuilder query = new StringBuilder();
        query.append("DROP TABLE IF EXISTS " + DATABASE_NAME);
        db.execSQL(query.toString());
        onCreate(db);
    }

    public ArrayList<Alarm> savedAlarms(){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Alarm> saved = new ArrayList<>();
        String sortOrder = COLUMNS[2] + " ASC";
        Cursor c = db.query(TABLE_NAME, COLUMNS, null,null,null,null,sortOrder);
        while(c.moveToNext()){
            Alarm a = new Alarm();
            a.setID(c.getLong(0));
            a.setAlarmName(c.getString(1));
            a.setAlarmTime(c.getString(2));
            a.setRingtone(c.getString(3));
            a.setType(c.getString(4));
            a.setAlarmETA(c.getString(5));
            a.setActive((c.getInt(6) == 1));
            Event e = new Event();
            e.setLocation(c.getString(7));
            e.setDay_start(c.getString(8));
            e.setDay_end(c.getString(9));
            e.setDuration(c.getString(10));
            a.setEvent(e);
            saved.add(a);
        }
        c.close();

        return saved;
    }

    public boolean insertAlarm(Alarm a){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMNS[1], a.getAlarmName());
        contentValues.put(COLUMNS[2], a.getAlarmTime());
        contentValues.put(COLUMNS[3], a.getRingtone());
        contentValues.put(COLUMNS[4], a.getType());
        contentValues.put(COLUMNS[5], a.getAlarmETA());
        contentValues.put(COLUMNS[6], (a.isActive() == true) ? 1 : 0);
        contentValues.put(COLUMNS[7], a.getEvent().getLocation());
        contentValues.put(COLUMNS[8], a.getEvent().getDay_start());
        contentValues.put(COLUMNS[9], a.getEvent().getDay_end());
        contentValues.put(COLUMNS[10], a.getEvent().getDuration());
        long result = db.insert(TABLE_NAME, null, contentValues);
        return result != -1;
    }

    public boolean updateAlarm(Alarm a){
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMNS[1], a.getAlarmName());
        contentValues.put(COLUMNS[2], a.getAlarmTime());
        contentValues.put(COLUMNS[3], a.getRingtone());
        contentValues.put(COLUMNS[4], a.getType());
        contentValues.put(COLUMNS[5], a.getAlarmETA());
        contentValues.put(COLUMNS[6], (a.isActive() == true) ? 1 : 0);
        contentValues.put(COLUMNS[7], a.getEvent().getLocation());
        contentValues.put(COLUMNS[8], a.getEvent().getDay_start());
        contentValues.put(COLUMNS[9], a.getEvent().getDay_end());
        contentValues.put(COLUMNS[10], a.getEvent().getDuration());

        String selection = COLUMNS[0] + " = ? ";
        String[] selectionArgs = {String.valueOf(a.getID())};
        long result = db.update(TABLE_NAME, contentValues, selection, selectionArgs);
        return result != -1;
    }

    public void deleteAlarm(long id){
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = COLUMNS[0] + " = ? ";
        String[] selectionArgs = { String.valueOf(id)};
        db.delete(TABLE_NAME,selection,selectionArgs);
    }
}
