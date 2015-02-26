package ru.perm.trubnikov.clicklight;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

public class NotifyActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Utils.startSvc(NotifyActivity.this, "toggle");

        finish();

    }

}