package com.ajitsinghkamal.ichallenge;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v4.app.LoaderManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.ajitsinghkamal.ichallenge.data.*;

import org.w3c.dom.Text;

import java.util.Calendar;

public class Main2Activity extends AppCompatActivity implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor>{

    private static int id,duration;
    private taskAdapter adapter;
    private Cursor today;
    private Uri progressChart=challengeContract.Progress.CONTENT_URI;

    private static final String TAG="checkpoint";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //get intent from MainActivity once challenge has been created
        //intent comes with extra baggage
        //it holds the _id of the created challenge row
        Intent in = getIntent();
        if (in != null){
            if(in.hasExtra("challengeId"))
                 id = in.getIntExtra("challengeId", 1);
            if(in.hasExtra("duration"))
                duration=in.getIntExtra("duration",21);
        }

        //start loader manager
        getSupportLoaderManager().initLoader(1, null, this);

        setContentView(R.layout.activity_main2);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //get grid
        GridView gridView = (GridView) findViewById(R.id.days_grid);
        //initaialise cursor adapter
        //passed null since loader isnt started yet
        adapter = new taskAdapter(getApplicationContext(), null, 0);

        gridView.setAdapter(adapter);

        //building query here
        //columns to get
        String[] qProjection = {
                challengeContract.Progress._ID,
                challengeContract.Progress.COLUMN_C_DAY
        };
        //where clause
        final String clause = challengeContract.Progress.COLUMN_TODAY + " = ? ";
        //argument to pass to clause
        final String[] arg = {"1"};
        //get active day from grid
        today = getContentResolver().query(progressChart, qProjection, clause, arg, null);
        //move to first row in cursor
        today.moveToFirst();
        //making each grid item clickable
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //action on click according to active status of grid item
                //matching it to position of item in grid
                int currentDay = today.getInt(today.getColumnIndex(challengeContract.Progress.COLUMN_C_DAY));

                if (position +1 == currentDay) {
                    Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_SHORT).show();
                    ContentValues values = new ContentValues();
                    values.put(challengeContract.Progress.COLUMN_STATUS, 1);
                    getContentResolver().update(progressChart, values, clause, arg);
                }
                else if (position + 1 < currentDay) {
                    Toast.makeText(getApplicationContext(), "you'll need a time turner", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getApplicationContext(), "present is more precious", Toast.LENGTH_SHORT).show();
                }
            }

        });

        TextView accomplished=(TextView)findViewById(R.id.done_count);
        TextView stillToGo=(TextView)findViewById(R.id.to_conquer);
        Cursor c=getContentResolver().query(progressChart,
                qProjection,
                challengeContract.Progress.COLUMN_STATUS+" = ? ",
                new String[]{"1"},
                null
                );
        accomplished.setText(Integer.toString(c.getCount()));
        int left=0;
        if(c.getCount()>0) {
            c.moveToFirst();
            left = duration - c.getInt(1);
        }
        stillToGo.setText(Integer.toString(left));
        c.close();

        startAlarm();

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i,Bundle bundle){

        String[] projections={challengeContract.Progress._ID,
                challengeContract.Progress.COLUMN_C_DAY,
                challengeContract.Progress.COLUMN_STATUS,
                challengeContract.Progress.COLUMN_TODAY
        };
        return new CursorLoader(getApplicationContext(),progressChart,projections,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> curserloader,Cursor cursor){
        adapter.changeCursor(cursor);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        adapter.changeCursor(null);

    }

    //start Alarm Manager
   public void startAlarm(){
       Intent alarm = new Intent(this,JobSchedularService.class);
       PendingIntent pendingIntent = PendingIntent.getBroadcast(this,0,alarm,0);
       AlarmManager manager=(AlarmManager)getSystemService(Context.ALARM_SERVICE);
       //fire at every 24 hr
       manager.setInexactRepeating(AlarmManager.RTC, SystemClock.elapsedRealtime()+AlarmManager.INTERVAL_DAY,
               AlarmManager.INTERVAL_DAY,pendingIntent);

   }
}
