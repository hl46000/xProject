package com.purehero.ftp.client;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.preference.SwitchPreference;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Window;
import android.widget.EditText;

/**
 * Created by purehero on 2017-03-13.
 */

public class FtpClientSettingsActivity extends PreferenceActivity {
    static String root_folder = "/";
    static SwitchPreference ftpServerSwitch = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
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
            addPreferencesFromResource(R.xml.ftp_client_settings);

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
            }
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            updatePrefSummary(findPreference(key));
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            boolean checked = ((SwitchPreference) preference).isChecked();
            Log.d( "MyLOG", String.format( "onPreferenceClick %s", checked ? "checked" : "not checked" ));

            return true;
        }
    }
}
