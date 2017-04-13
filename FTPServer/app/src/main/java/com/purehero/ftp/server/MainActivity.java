package com.purehero.ftp.server;


import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.preference.SwitchPreference;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.Window;
import android.widget.EditText;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class MainActivity extends PreferenceActivity {
    static String root_folder = "/";
    static SwitchPreference ftpServerSwitch = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        //this.setContentView(R.layout.ftp_server_settings);

        //root_folder = this.getIntent().getStringExtra("lastFolder");
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

            Preference preference = findPreference( "ftp_server_addr" );
            if( preference != null ) {
                preference.setSummary( G.getIPAddress(true) );
            }

            preference = findPreference( "ftp_server_home" );
            if( preference != null ) {
                preference.setSummary( Environment.getExternalStorageDirectory().getAbsolutePath() );
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            checkPermission();
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
            switch (requestCode) {
                case 123 :
                /*
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    checkPermission();
                }
                */
                    break;
            }
        }

        private int checkPermission() {
            String permissions[] = {
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_NETWORK_STATE
            };
            List<String> request_permissions = new ArrayList<String>();
            for( String permission : permissions ) {
                if (ContextCompat.checkSelfPermission(this.getActivity(), permission) != PackageManager.PERMISSION_GRANTED ) {
                    request_permissions.add( permission );
                }
            }

            if( request_permissions.size() > 0 ) {
                String permissionsList [] = new String[request_permissions.size()];
                request_permissions.toArray(permissionsList);
                ActivityCompat.requestPermissions(this.getActivity(), permissionsList, 123 );
            }

            return request_permissions.size();
        }

        private void initSummary(Preference p) {
            if (p instanceof PreferenceGroup) {
                PreferenceGroup pGrp = (PreferenceGroup) p;
                for (int i = 0; i < pGrp.getPreferenceCount(); i++) {
                    initSummary(pGrp.getPreference(i));
                }
            } else {
                if( p instanceof SwitchPreference ) {
                    ftpServerSwitch = ( SwitchPreference ) p;

                    if( MyFtpServer.getInstance(getActivity()).isStartedFtpServer()) {
                        ftpServerSwitch.setTitle( R.string.ftp_server_running );
                        ftpServerSwitch.setChecked( true );
                    } else {
                        ftpServerSwitch.setTitle( R.string.ftp_server_stoped );
                        ftpServerSwitch.setChecked( false );
                    }
                    update_server_address.run();
                } else {
                    updatePrefSummary(p);
                }
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
            }
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            updatePrefSummary(findPreference(key));
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            boolean checked = ((SwitchPreference) preference).isChecked();
            G.Log( "onPreferenceClick %s", checked ? "checked" : "not checked" );

            final MyFtpServer myFtpServer = MyFtpServer.getInstance(getActivity());

            if( checked ) {
                if( myFtpServer.isStartedFtpServer()) {
                    myFtpServer.stopFtpServer();
                }
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                String strPort  = sharedPref.getString( "ftp_server_port", "2345" );
                String userID   = sharedPref.getString( "ftp_server_user_id", "Guest" );
                String userPWD  = sharedPref.getString( "ftp_server_user_pwd", "1234" );
                String homeDir  = sharedPref.getString( "ftp_server_home", "/" );

                myFtpServer.initFtpServer( userID, userPWD, Integer.valueOf( strPort ), new File( homeDir ));
                myFtpServer.startFtpServer();
            } else {
                myFtpServer.stopFtpServer();
            }

            new Handler().postDelayed( update_server_address, 1000 );
            return true;
        }

        Runnable update_server_address = new Runnable() {
            @Override
            public void run() {
                Preference ca = findPreference( "connection_address" );

                if( MyFtpServer.getInstance( getActivity() ).isStartedFtpServer()) {
                    ftpServerSwitch.setTitle( R.string.ftp_server_running );

                    if( ca != null ) {
                        ca.setSummary( MyFtpServer.getInstance( getActivity() ).getConnectionMessage());
                    }
                } else {
                    ftpServerSwitch.setTitle( R.string.ftp_server_stoped );

                    if( ca != null ) {
                        ca.setSummary( "unknown" );
                    }
                }
            }
        };
    }
}
