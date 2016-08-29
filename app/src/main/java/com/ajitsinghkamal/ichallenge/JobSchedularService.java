package com.ajitsinghkamal.ichallenge;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import com.ajitsinghkamal.ichallenge.data.*;

/**
 * Service to perform auto update of active day in grid everyday after midnight
 */
public class JobSchedularService extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent){
        //get current active day
        String[] qProjection = {
                challengeContract.Progress._ID,
                challengeContract.Progress.COLUMN_C_DAY
        };

        final String clause = challengeContract.Progress.COLUMN_TODAY + " = ? ";
        final String[] arg = {"1"};
        Cursor check = context.getContentResolver().query(challengeContract.Progress.CONTENT_URI, qProjection, clause,arg,null);

        if(check.moveToFirst()) {
            //use the current active day to get the row to update
            int pos = check.getInt(1) + 1;
            //argument for update clause
            String[] p = {Integer.toString(pos)};

            ContentValues val = new ContentValues();
            //set current active day as not active
            val.put(challengeContract.Progress.COLUMN_TODAY,0);
            //update current day
            context.getContentResolver().update(challengeContract.Progress.CONTENT_URI, val, clause, arg);
            val.clear();
            //set next day as active
            val.put(challengeContract.Progress.COLUMN_TODAY, 1);
            //update next day
            context.getContentResolver().update(challengeContract.Progress.CONTENT_URI,
                    val,
                    challengeContract.Progress.COLUMN_C_DAY + " = ? ",
                    p);
        }

    }
}

