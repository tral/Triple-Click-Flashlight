package ru.perm.trubnikov.clicklight;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

@TargetApi(11)
public class PreferencesActivity extends ActionBarActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTitle(R.string.action_settings); // otherwise it's not changed

        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new PrefsFragment()).commit();

        ShowBackButton();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void ShowBackButton() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // call something for API Level 11+
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }


}