package com.ajitsinghkamal.ichallenge;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.ajitsinghkamal.ichallenge.data.challengeContract;


public class FirstActivity extends AppCompatActivity {

    private Cursor cursor;

    SharedPreferences prefs = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        FloatingActionButton fab=(FloatingActionButton)findViewById(R.id.fab);
        TextView bt=(TextView)findViewById(R.id.goTwo);

        prefs = getSharedPreferences("firstRunPref", MODE_PRIVATE);
        boolean firstRun=prefs.getBoolean("firstRun",true);

        if(firstRun){
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i=new Intent(FirstActivity.this ,MainActivity.class);
                    startActivity(i);
                }
            });
            SharedPreferences.Editor editor = prefs.edit(); // Open the editor for our settings
            editor.putBoolean("firstRun", false); // It is no longer the first run
            editor.commit();
        }
        else{
            String[] projection={
                    challengeContract.Challenge._ID,
                    challengeContract.Challenge.COLUMN_C_NAME
            };

            cursor=getContentResolver().query(challengeContract.Challenge.CONTENT_URI,
                    projection,
                    challengeContract.Challenge.COLUMN_STATUS+" = ? ",
                    new String[]{"0"},
                    null);

            assert fab!=null;
            if(cursor.getCount()>0){
                fab.setBackgroundResource(R.drawable.grid_border);
                cursor.moveToLast();
                bt.setText("Track challenge");

            }

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Launch settings activity to set up new challenge
                    if(cursor.getCount()>0) {
                        Intent i = new Intent(FirstActivity.this, Main2Activity.class);
                        startActivity(i);
                    }
                    else{
                        Intent i=new Intent(FirstActivity.this ,MainActivity.class);
                        startActivity(i);
                    }

                }
            });

            cursor.close();


        }


    }

}
