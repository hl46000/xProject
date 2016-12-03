package com.purehero.prj01.androidmanager;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

public class SettingsActivity extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
				
		getFragmentManager()
			.beginTransaction()
			.replace(android.R.id.content,
            new MyPreferenceFragment()).commit();
	}

	// PreferenceFragment ?��?��?�� ?��?��
    public static class MyPreferenceFragment extends
            PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_settings);
        }
    }
}
