package ru.perm.trubnikov.clicklight;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;

public class ClickFlashService extends Service {

    final String LOG_TAG = "ClickFlash";

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF) || intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                handleClick();
            }
        }
    };

    protected void handleClick() {

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        long cur = System.currentTimeMillis();
        long diff = Utils.diff3(settings.getLong("preLastClick", 0), settings.getLong("lastClick", 0), cur);
/*
        Log.d(LOG_TAG, settings.getLong("preLastClick", 0) + " -pre");
        Log.d(LOG_TAG, settings.getLong("lastClick", 0) + " -last");
        Log.d(LOG_TAG, cur + " -cur");
        Log.d(LOG_TAG, diff + " -diff");*/

        if (diff > 0 && diff < Integer.parseInt(settings.getString("prefInterval", "2000"))) {
            Utils.flashlightToggle(getApplicationContext());
            if (settings.getBoolean("prefVibrate", true)) {
                Vibrator v = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(200);
            }
            cur = 0;
        }

        SharedPreferences.Editor editor = settings.edit();
        editor.putLong("preLastClick", settings.getLong("lastClick", 0));
        editor.putLong("lastClick", cur);
        editor.commit();
    }


    public void onCreate() {
        super.onCreate();

        Log.d(LOG_TAG, "onCreate");

        final IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);

        registerReceiver(receiver, filter);

    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    public void onDestroy() {
        if (receiver != null) {
            unregisterReceiver(receiver);
        }

        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy");
    }

    public IBinder onBind(Intent intent) {
        Log.d(LOG_TAG, "onBind");
        return null;
    }
}
