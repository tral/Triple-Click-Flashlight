package ru.perm.trubnikov.clicklight;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.melnykov.fab.FloatingActionButton;


public class MainActivity extends ActionBarActivity {

    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startService(new Intent(this, ClickFlashService.class));

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("ListViewFragment", "OnClickListener()");
                Utils.flashlightToggle(getApplicationContext());
                updateFab();

            }
        });

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (settings.getBoolean("isFirstRun", true)) {
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("isFirstRun", false).commit();
            new AlertDialog.Builder(this).setTitle(getString(R.string.hello_str)).setMessage(getString(R.string.first_run_content)).setNeutralButton(getString(R.string.its_clear_btn), null).show();
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        updateFab();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent sett_intent;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                sett_intent = new Intent(this, PreferencesActivity.class);
            } else {
                sett_intent = new Intent(this, PreferencesLegacyActivity.class);
            }
            startActivity(sett_intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateFab() {
        if (Utils.isFlashOn()) {
            fab.setImageResource(R.drawable.ic_sunny_s);
        } else {
            fab.setImageResource(R.drawable.ic_sunny_g);
        }
    }
}
