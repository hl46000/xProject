package com.purehero.bluetooth.share;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.purehero.bluetooth.share.contacts.ContactAdapter;
import com.purehero.bluetooth.share.contacts.ContactFragment;
import com.purehero.module.fragment.FragmentEx;

/**
 * Created by MY on 2017-02-25.
 */
public class HomeFragment extends FragmentEx implements View.OnClickListener {
    private MainActivity context = null;
    private View layout = null;

    public HomeFragment setMainActivity(MainActivity mainActivity) {
        context = mainActivity;

        return this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        G.Log( "onCreateView" );

        // Fragment 가 option menu을 가지고 있음을 알림
        setHasOptionsMenu(true);

        // ActionBar Title 변경
        AppCompatActivity ACActivity = ( AppCompatActivity ) getActivity();
        ActionBar aBar = ACActivity.getSupportActionBar();
        if( aBar != null ) {
            aBar.setTitle( R.string.app_name );
        }

        layout 	= inflater.inflate( R.layout.home_layout, container, false );

        int btnIDs[] = {R.id.btnApps, R.id.btnAudios, R.id.btnContacts, R.id.btnDocuments, R.id.btnDownloads, R.id.btnMyFiles, R.id.btnPhotos, R.id.btnVideos };
        for( int id : btnIDs ) {
            LinearLayout btn = (LinearLayout ) layout.findViewById( id );
            if( btn != null ) {
                btn.setOnClickListener( this );
            }
        }

        return layout ;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        context.replaceFragmentById( id );
    }
}
