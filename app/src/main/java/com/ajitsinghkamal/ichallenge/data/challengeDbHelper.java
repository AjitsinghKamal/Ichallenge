package com.ajitsinghkamal.ichallenge.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.ajitsinghkamal.ichallenge.data.challengeContract.*;
/**
 * Created by deku on 13/8/16.
 */
public class challengeDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION=1;
    static final String DATABASE_NAME="challenge.db";

    public challengeDbHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase){
        final String SQL_CREATE_CHALLENGE_TABLE="CREATE TABLE "+ Challenge.TABLE_NAME+"("+
                Challenge._ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
                Challenge.COLUMN_C_NAME+" TEXT NOT NULL,"+
                Challenge.COLUMN_DUR+" INTEGER NOT NULL,"+
                Challenge.COLUMN_MODE+" INTEGER NOT NULL,"+
                Challenge.COLUMN_STATUS+" INTEGER NOT NULL"+
                ");";


        final String SQL_CREATE_PROGRESS_TABLE="CREATE TABLE "+Progress.TABLE_NAME+"("+
                Progress._ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
                Progress.COLUMN_C_ID+" INTEGER NOT NULL,"+
                Progress.COLUMN_C_DAY+" INTEGER NOT NULL,"+
                Progress.COLUMN_STATUS+" INTEGER NOT NULL,"+
                Progress.COLUMN_TODAY+" INTEGER,"+
                "FOREIGN KEY ("+Progress.COLUMN_C_ID+") REFERENCES "+
                Challenge.TABLE_NAME+" ("+Challenge._ID+")"+
                ");";
        sqLiteDatabase.execSQL(SQL_CREATE_CHALLENGE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_PROGRESS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase,int oldVersion,int newVersion){
        sqLiteDatabase.execSQL("DROP TABLE IF EXIST"+Challenge.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXIST"+Progress.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

}
