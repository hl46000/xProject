package com.example.apk.extractor;

import com.example.appsealingexternalexecutor.R;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

public class SettingActivity extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
				
		getFragmentManager()
			.beginTransaction()
			.replace(android.R.id.content,
            new MyPreferenceFragment()).commit();
	}

	// PreferenceFragment 클래스 사용
    public static class MyPreferenceFragment extends
            PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_settings);
        }
    }
}
