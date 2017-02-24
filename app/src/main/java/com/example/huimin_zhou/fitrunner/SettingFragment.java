package com.example.huimin_zhou.fitrunner;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by Lucidity on 17/1/17.
 */

public class SettingFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        addPreferencesFromResource(R.xml.settingpreference);
    }
}