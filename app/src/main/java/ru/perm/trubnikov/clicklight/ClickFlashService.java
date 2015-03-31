package ru.perm.trubnikov.clicklight;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;

public class ClickFlashService extends Service {

    final String LOG_TAG = "ClickFlash";
    final static String BROADCAST_ACTION = "ru.perm.trubnikov.clicklight.action";
    public static final int FOREGROUND_SERVICE_ID = 25375;
    public static final int BROADCAST_FLASH_TOGGLED = 1;
    private final ScreenReceiver receiver = new ScreenReceiver();
/*
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF) || intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                handleClick();
            }
        }
    };*/

    protected void handleClick() {

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        long cur = System.currentTimeMillis();
        long diff = Utils.diff3(settings.getLong("preLastClick", 0), settings.getLong("lastClick", 0), cur);
/*
        Log.d(LOG_TAG, "------------------------");
        Log.d(LOG_TAG, settings.getLong("preLastClick", 0) + " -pre");
        Log.d(LOG_TAG, settings.getLong("lastClick", 0) + " -last");
        Log.d(LOG_TAG, cur + " -cur");
        Log.d(LOG_TAG, diff + " -diff");*/

        if (diff > 0 && diff < Integer.parseInt(settings.getString("prefInterval", "2000"))) {
            flashToggle(settings.getBoolean("prefVibrate", true));
            resetTimeStamps(settings);
        } else {
            updateTimeStamps(settings, cur);
        }


    }

    // Sends update message to activity (for updating FAB image)
    protected void sendMessageToActivity() {
        Intent intent = new Intent();
        intent.setAction(BROADCAST_ACTION);
        intent.putExtra("DATAPASSED", BROADCAST_FLASH_TOGGLED);
        sendBroadcast(intent);
    }

    protected void updateTimeStamps(SharedPreferences settings, long cur) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong("preLastClick", settings.getLong("lastClick", 0));
        editor.putLong("lastClick", cur);
        editor.commit();
    }

    protected void resetTimeStamps(SharedPreferences settings) {
        updateTimeStamps(settings, 0);
    }

    protected void flashToggle(boolean isVibrate) {

        Utils.flashlightToggle(getApplicationContext());

        if (Utils.isFlashOn()) {
            startForeground(FOREGROUND_SERVICE_ID, Utils.getNotification(getApplicationContext()));
        } else {
            stopForeground(true);
        }

        if (isVibrate) {

            try {
                final Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(200);
                // Possible constant vibration fix?
                /* final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Do something after 200ms (avoid random vibrations, Nexus 5)
                        v.cancel();
                    }
                }, 200);*/

            } catch (Exception e) {

            }
        }

        sendMessageToActivity();
    }

    public void onCreate() {
        super.onCreate();

        final IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(receiver, filter);

        Log.d(LOG_TAG, "onCreate");
    }

    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null) {
            Bundle b = intent.getExtras();
            String cmd = b.getString("cmd");

            if (cmd.equalsIgnoreCase("toggle")) {
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

                // Если установили в настройках запрет срабатывания при вызове и идет вызов - не включаем фонарик
                if (!(settings.getBoolean("prefBlockIfInCall", true) && Utils.isCallActive(getApplicationContext()))) {
                    flashToggle(settings.getBoolean("prefVibrate", true));
                }

                resetTimeStamps(settings);
            } else if (cmd.equalsIgnoreCase("scr_on") || cmd.equalsIgnoreCase("scr_off")) {
                handleClick();
            }

        }

        Log.d(LOG_TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
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
