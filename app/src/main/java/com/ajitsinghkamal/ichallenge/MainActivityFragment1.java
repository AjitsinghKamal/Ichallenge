package com.ajitsinghkamal.ichallenge;

import android.app.Activity;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment1 extends DialogFragment {

    NumberPicker picker;

    public interface NumberPickedListener{
        public void onNumberPicked(NumberPicker num);
    }
    NumberPickedListener mCallBack;

    public MainActivityFragment1() {
    }

    static MainActivityFragment1 newInstance(){
        return new MainActivityFragment1();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view= inflater.inflate(R.layout.fragment_main, container, false);

        getDialog().setCanceledOnTouchOutside(true);

        picker=(NumberPicker) view.findViewById(R.id.dialog_picker);
        picker.setMaxValue(32);
        picker.setMinValue(4);

        Button done=(Button) view.findViewById(R.id.dialog_done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallBack.onNumberPicked(picker);
                dismiss();
            }
        });




        return view;
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        try{
            mCallBack=(NumberPickedListener) activity;
        }catch (ClassCastException e){
            throw new ClassCastException(activity.toString()+"must implement NumberPickedListener");
        }
    }
}
