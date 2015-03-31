package ru.perm.trubnikov.clicklight;

import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

@TargetApi(11)
public class PrefsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.settings_checkbox_or_switch);
        addPreferencesFromResource(R.xml.settings_about);

        String version = "?";
        try {
            version = getActivity().getApplicationContext().getPackageManager().getPackageInfo(getActivity().getApplicationContext().getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        Preference pref = findPreference("prefAbout");
        pref.setSummary(getString(R.string.pref_about_summary) + " " + version);

    }


}