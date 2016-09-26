
/*
 * Copyright (c) 2016. This app was made by Otavio Tarelho and Diego Nunes as requirement to get their major certificate. Any copy of this project will suffer legal penalties under Copyrights Laws.
 */

package com.tempus.auxiliars;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBAdapter {

    private static final String DB_NAME = "";
    private static final int DB_VERSION = 1;
    private static final String DB_TABLE = "";
    private static final String CREATE_TABLE_ALARM = "";
    private Context context;
    private SQLiteDatabase sqlDB;
    private TempusDBAdapterHelper tempusAdapter;

    //
    //Methods
    //

    // Contructor
    public DBAdapter (Context cx) { context = cx; }

    //Method to close Database connection
    public void close() { tempusAdapter.close(); }

    // Method to open Database connection
    public DBAdapter open() throws android.database.SQLException {

        tempusAdapter = new TempusDBAdapterHelper(context);
        sqlDB = tempusAdapter.getWritableDatabase();
        return this;

    }//end of method Open()


    public static class TempusDBAdapterHelper extends SQLiteOpenHelper {

        public TempusDBAdapterHelper(Context ctx){

            super(ctx, DB_NAME, null, DB_VERSION);

        } // end of constructor

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {

            sqLiteDatabase.execSQL(CREATE_TABLE_ALARM);

        } // end of onCreate()

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

            sqLiteDatabase.execSQL("DROP TABLE IF EXIST" + DB_TABLE);
            onCreate(sqLiteDatabase);

        } // end of onUpgrade()

    } // End of thread class TempusDBAdapterHelper

} // end of Class
