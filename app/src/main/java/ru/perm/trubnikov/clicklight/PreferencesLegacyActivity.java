package ru.perm.trubnikov.clicklight;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;

public class PreferencesLegacyActivity extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {

        setTitle(R.string.action_settings); // otherwise it's not changed

        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings_checkbox_or_switch);
        addPreferencesFromResource(R.xml.settings_about);

        String version="?";
        try {
            version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        Preference pref = findPreference("prefAbout");
        pref.setSummary(getString(R.string.pref_about_summary) + " " + version);

    }

}