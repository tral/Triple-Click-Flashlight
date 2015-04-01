package ru.perm.trubnikov.clicklight;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

public class NotifyActivity extends ActionBarActivity {

    final String LOG_TAG = "ClickFlash";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Log.d(LOG_TAG, "NotifyActivity:onCreate ---->");
        Utils.startSvc(NotifyActivity.this, "toggle");

        finish();

    }

}