package com.purehero.ftpserver.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.annotation.Nullable;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;

import share.file.purehero.com.fileshare.MyFtpServer;
import share.file.purehero.com.fileshare.R;

/**
 * Created by purehero on 2017-03-13.
 */

public class FtpServerSettingsActivity extends PreferenceActivity {
    static String root_folder = "/";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this.setContentView(R.layout.ftp_server_settings);

        root_folder = this.getIntent().getStringExtra("lastFolder");
        getFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content,
                        new MyPreferenceFragment()).commit();
    }

    public static class MyPreferenceFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceClickListener {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.ftp_server_settings);

            SwitchPreference ftpServerSwitch = (SwitchPreference) findPreference("ftp_server_status");
            if( ftpServerSwitch != null ) {
                ftpServerSwitch.setOnPreferenceClickListener( this );
            }
            initSummary(getPreferenceScreen());
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }

        private void initSummary(Preference p) {
            if (p instanceof PreferenceGroup) {
                PreferenceGroup pGrp = (PreferenceGroup) p;
                for (int i = 0; i < pGrp.getPreferenceCount(); i++) {
                    initSummary(pGrp.getPreference(i));
                }
            } else {
                updatePrefSummary(p);
            }
        }

        private void updatePrefSummary(Preference p) {
            if (p instanceof ListPreference) {
                ListPreference listPref = (ListPreference) p;
                p.setSummary(listPref.getEntry());

            } else if (p instanceof EditTextPreference) {
                EditTextPreference editTextPref = (EditTextPreference) p;

                EditText edit = ((EditTextPreference) p).getEditText();
                String pref = edit.getTransformationMethod().getTransformation(editTextPref.getText(), edit).toString();
                p.setSummary(pref);

            } else if (p instanceof MultiSelectListPreference) {
                EditTextPreference editTextPref = (EditTextPreference) p;
                p.setSummary(editTextPref.getText());

            } else if( p instanceof SwitchPreference ) {
                SwitchPreference switchPref = ( SwitchPreference ) p;
                //if( switchPref.getKey() == "ftp_server_status" )
                {
                    if( MyFtpServer.getInstance(getActivity()).isStartedFtpServer()) {
                        switchPref.setTitle( R.string.ftp_server_running );
                        switchPref.setChecked( true );
                    } else {
                        switchPref.setTitle( R.string.ftp_server_stoped );
                        switchPref.setChecked( false );
                    }
                }
            }
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            updatePrefSummary(findPreference(key));
        }

        @Override
        public boolean onPreferenceClick(final Preference preference) {
            boolean checked = ((SwitchPreference) preference).isChecked();
            final MyFtpServer myFtpServer = MyFtpServer.getInstance(getActivity());

            if( checked ) {
                if( myFtpServer.isStartedFtpServer()) {
                    myFtpServer.stopFtpServer();
                }
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                String strPort  = sharedPref.getString( "tfp_server_port", "2345" );
                String userID   = sharedPref.getString( "ftp_server_user_id", "Guest" );
                String userPWD  = sharedPref.getString( "ftp_server_user_pwd", "1234" );

                myFtpServer.initFtpServer( userID, userPWD, Integer.valueOf( strPort ), new File( root_folder ));
                myFtpServer.startFtpServer();
            } else {
                myFtpServer.stopFtpServer();
            }

            new Handler().postDelayed(new Runnable(){
                @Override
                public void run() {
                    getActivity().runOnUiThread( new Runnable(){
                        @Override
                        public void run() {
                            Preference ca = findPreference( "connection_address" );

                            if( myFtpServer.isStartedFtpServer()) {
                                preference.setTitle( R.string.ftp_server_running );

                                if( ca != null ) {
                                    ca.setSummary( myFtpServer.getConnectionMessage());
                                }
                            } else {
                                preference.setTitle( R.string.ftp_server_stoped );

                                if( ca != null ) {
                                    ca.setSummary( "unknown" );
                                }
                            }
                        }
                    });
                }
            }, 1000 );
            return true;
        }
    }
}
