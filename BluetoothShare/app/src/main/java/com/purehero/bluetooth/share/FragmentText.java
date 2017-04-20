package com.purehero.bluetooth.share;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.purehero.module.fragment.FragmentEx;

/**
 * Created by MY on 2017-02-25.
 */
public class FragmentText extends FragmentEx {
    String text_message = "Fragment content";
    public FragmentText setText( String text_message ) {
        this.text_message = text_message;
        return this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        TextView text = new TextView(container.getContext());
        text.setText( text_message );
        text.setGravity(Gravity.CENTER);

        AppCompatActivity ACActivity = ( AppCompatActivity ) getActivity();
        ActionBar aBar = ACActivity.getSupportActionBar();
        if( aBar != null ) {
            aBar.setTitle( text_message.split(" ")[0] );
        }

        return text;
    }
}
