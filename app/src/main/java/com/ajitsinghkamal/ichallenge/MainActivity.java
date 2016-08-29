package com.ajitsinghkamal.ichallenge;
import com.ajitsinghkamal.ichallenge.data.challengeContract;
import com.ajitsinghkamal.ichallenge.data.challengeDbHelper;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class MainActivity extends AppCompatActivity implements MainActivityFragment1.NumberPickedListener {
    private static final String TAG="SetUpActivity";
    TextView duration;
    TextView lazyMode;
    TextView strictmode;
    EditText title;
    int numberDays=21;
    int difficulty;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        RelativeLayout window=(RelativeLayout) findViewById(R.id.main_relative2);
        try {
            window.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialog();
                    // duration.setText(numberDays);
                }
            });
        }catch(NullPointerException e){
            Log.e(TAG,"time window in MainActivity broke");
        }
        duration=(TextView) findViewById(R.id.main_text3);
        lazyMode=(TextView) findViewById(R.id.main_switchLazy);
        strictmode=(TextView) findViewById(R.id.main_switchStrict);
        Switch mode=(Switch) findViewById(R.id.main_switch);
        title=(EditText) findViewById(R.id.main_edit1);
        difficulty=0;
        if(mode!=null){
            mode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if(!isChecked){
                        difficulty=1;
                        lazyMode.setTextColor(ContextCompat.getColor(MainActivity.this,R.color.colorAccentSec));
                        strictmode.setTextColor(ContextCompat.getColor(MainActivity.this,R.color.colorSecondary));
                    }
                    else{
                        difficulty=0;
                        strictmode.setTextColor(ContextCompat.getColor(MainActivity.this,R.color.colorAccentSec));
                        lazyMode.setTextColor(ContextCompat.getColor(MainActivity.this,R.color.colorSecondary));
                    }
                }
            });
        }



        Button submit=(Button)findViewById(R.id.main_submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });



    }

    public void submit(){
        //ToDO:remove this toast


        ContentValues values=new ContentValues();

        //---------------------insert new challenge row------------------------------------
        Toast.makeText(getApplicationContext(),"control",Toast.LENGTH_SHORT).show();

        values.put(challengeContract.Challenge.COLUMN_C_NAME,title.getText().toString());
        values.put(challengeContract.Challenge.COLUMN_DUR,numberDays);
        values.put(challengeContract.Challenge.COLUMN_MODE,difficulty);
        values.put(challengeContract.Challenge.COLUMN_STATUS,0);

        Uri mUri=getContentResolver().insert(challengeContract.Challenge.CONTENT_URI,values);

        //TODO:remove this toast
         Toast.makeText(getApplicationContext(),"inserted "+mUri,Toast.LENGTH_LONG).show();

        //clearing values to use in creating progress


        //-----------------------create progress table ---------------------------------------


            int challengeId = (int) ContentUris.parseId(mUri);
            int countDay = 1;
            for (int nRow = 0; nRow < numberDays; nRow++) {
                ContentValues val=new ContentValues();
                val.put(challengeContract.Progress.COLUMN_C_ID, challengeId);
                val.put(challengeContract.Progress.COLUMN_C_DAY, countDay);
                val.put(challengeContract.Progress.COLUMN_STATUS, 0);
                if(nRow==0) {
                    val.put(challengeContract.Progress.COLUMN_TODAY, 1);
                }
                else {
                    val.put(challengeContract.Progress.COLUMN_TODAY, 0);
                }

                getContentResolver().insert(challengeContract.Progress.CONTENT_URI, val);
                countDay++;
            }



        //------------------------fire up new intent to launch My2Activity----------------------

            Intent main2activity = new Intent(MainActivity.this, Main2Activity.class);
            //pass challengeId as extra text to main2activity
            main2activity.putExtra("challengeId",challengeId);
            main2activity.putExtra("duration",numberDays);
            startActivity(main2activity);

    }

    public void onNumberPicked(NumberPicker picked){
        try {
            numberDays = picked.getValue();
        }catch(Error e){
            Log.e(TAG,"error ocurred "+ e);
        }
        duration.setText(String.valueOf(numberDays));
    }


    void showDialog(){
        DialogFragment dialog=MainActivityFragment1.newInstance();
        dialog.show(getSupportFragmentManager(),"Pick duration");
    }


}
