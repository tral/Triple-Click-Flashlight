package ru.perm.trubnikov.clicklight;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.melnykov.fab.FloatingActionButton;

import de.cketti.library.changelog.ChangeLog;

// TODO Donation in GP

public class MainActivity extends ActionBarActivity {

    FloatingActionButton fab;

    MyReceiver myReceiver;

    private class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            int datapassed = arg1.getIntExtra("DATAPASSED", 0);

            if (datapassed == ClickFlashService.BROADCAST_FLASH_TOGGLED) {
                updateFab();
            }

            Log.d("ClickFlash", "Triggered by Service "+ String.valueOf(datapassed));
        }
    }

    @Override
    public void onDestroy() {
        if (myReceiver != null) {
            unregisterReceiver(myReceiver);
        }

        super.onDestroy();
        Log.d("ClickFlash", "onDestroy");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Register BroadcastReceiver
        //to receive event from our service
        myReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ClickFlashService.BROADCAST_ACTION);
        registerReceiver(myReceiver, intentFilter);

        // Starting service
        Utils.startSvc(MainActivity.this, "none");

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Utils.startSvc(MainActivity.this, "toggle");
            }
        });

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (settings.getBoolean("isFirstRun", true)) {
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("isFirstRun", false).commit();
            new AlertDialog.Builder(this).setTitle(getString(R.string.hello_str)).setMessage(getString(R.string.first_run_content)).setNeutralButton(getString(R.string.its_clear_btn), null).show();
        }

        ChangeLog cl = new ChangeLog(this);
        if (cl.isFirstRun()) {
            cl.getLogDialog().show();
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
        int id = item.getItemId();

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

        if (id == R.id.action_donate) {
            Intent donate_intent = new Intent(this, DonateActivity.class);
            startActivity(donate_intent);
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
