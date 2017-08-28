package com.carporange.ichange.ui.activity;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.carporange.ichange.R;

public class SettingActivity extends PreferenceActivity  {


	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.activity_nav_setting);

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
