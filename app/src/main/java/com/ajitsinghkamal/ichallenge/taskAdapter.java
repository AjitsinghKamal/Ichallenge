package com.ajitsinghkamal.ichallenge;


import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import android.widget.TextView;
import com.ajitsinghkamal.ichallenge.data.challengeContract;


/**
 * Cursor Adapter to populate progress grid once a challenge is created
 */

public class taskAdapter extends CursorAdapter{

    private LayoutInflater cursorInflator;

    public taskAdapter(Context context,Cursor cursor,int flags){
        super(context,cursor,flags);
        cursorInflator=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public void bindView(View v,Context c,Cursor csr){
        //get view
        TextView text1=(TextView)v.findViewById(R.id.grid_item);
        //get column for each day from cursor
        String mainItem=csr.getString(csr.getColumnIndex(challengeContract.Progress.COLUMN_C_DAY));
        //get active column for today
        String subItem=csr.getString(csr.getColumnIndex(challengeContract.Progress.COLUMN_TODAY));
        //check status for each day
        //whether accomplished or not
        int status =csr.getInt(csr.getColumnIndex(challengeContract.Progress.COLUMN_STATUS));

        //some checks and ui changes
        //according to cursor returned

        //if the column is active as today
        //change its color and size
        //so user can easily identify
        if(subItem.equals("1")) {
            text1.setTextColor(Color.parseColor("#8ee3ef"));
            text1.setTextSize(20);
        }
        //change color and size for past and future event
        else{
            text1.setTextColor(Color.WHITE);
            text1.setTextSize(15);
        }
        //if marked as accomplished then identify it
       if(status==1)
           text1.setBackgroundResource(R.drawable.grid_item_border);
        //bind it to the grid
        text1.setText(mainItem);


    }

    @Override
    public View newView(Context c, Cursor csr, ViewGroup vg){
        //inflate grid for binding cursor data
       return cursorInflator.inflate(R.layout.task_grid, vg, false);

    }


}
