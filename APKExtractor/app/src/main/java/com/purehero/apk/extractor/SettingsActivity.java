package com.purehero.apk.extractor;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.os.Environment;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

import java.io.File;

public class SettingsActivity extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
				
		getFragmentManager()
			.beginTransaction()
			.replace(android.R.id.content,
            new MyPreferenceFragment()).commit();
	}

	
    public static class MyPreferenceFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_settings);

			File externalStorageFolder = Environment.getExternalStorageDirectory();
			File baseFile = new File( externalStorageFolder, "ApkExtractor" );

            SharedPreferences sp = getPreferenceScreen().getSharedPreferences();
            EditTextPreference editTextPref = (EditTextPreference) findPreference("sdcard_path");
            editTextPref.setSummary(sp.getString( "sdcard_path", baseFile.getAbsolutePath()));
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

		@Override
		public void onSharedPreferenceChanged(SharedPreferences arg0, String key) {
			Preference pref = findPreference(key);
	        if (pref instanceof EditTextPreference) {
	            EditTextPreference etp = (EditTextPreference) pref;
	            pref.setSummary(etp.getText());
	        }
		}
    }
}
