package ru.perm.trubnikov.clicklight;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class ClickFlashService extends Service {

    final String LOG_TAG = "ClickFlash";
    final static String BROADCAST_ACTION = "ru.perm.trubnikov.clicklight.action";
    public static final int FOREGROUND_SERVICE_ID = 25375;
    public static final int BROADCAST_FLASH_TOGGLED = 1;
    private final ScreenReceiver receiver = new ScreenReceiver();

    private Timer timer;

    /**
     * Функция, обрабатывающая переключение фонарика из одного состояния в другое
     */
    private void handleFlashToggle(SharedPreferences settings) {

        // Если установили в настройках запрет срабатывания при вызове и идет вызов - не включаем и не выключаем фонарик
        if (!(settings.getBoolean("prefBlockIfInCall", true) && Utils.isCallActive(getApplicationContext()))) {

            // Есть ли в настройках есть автоотключение?
            int auto_off = Integer.parseInt(settings.getString("prefAutooff", "5"));

            // Переключаем фонарик
            flashToggle(settings.getBoolean("prefVibrate", true));

            // Дополнительные действия в случае, когда юзер задал автоотключение фонарика
            if (auto_off > 0) {
                if (Utils.isFlashOn()) {
                    // Ветка для ВКЛЮЧЕНИЯ фонарика - устанавливаем таймер отключения
                    timer = new Timer();
                    timer.schedule(new timerTask(), auto_off * 60 * 1000);
                    Log.d(LOG_TAG, "---> Timer Sheduled: " + auto_off * 60 * 1000);
                } else {
                    // Ветка для ВЫКЛЮЧЕНИЯ фонарика - сносим таймер автоотключения
                    // т.к. мы сюда могли попасть как по таймеру автоотключения, так и при
                    // принудительном отключении фонарика юзером
                    try {
                        timer.cancel();
                    } catch (Exception e) {
                        Log.d(LOG_TAG, "---> timer.cancel(); EXCEPTION --->!");
                    }
                    Log.d(LOG_TAG, "---> Timer Cancelled!");
                }
            }
        }

        // В любом случае, при переключении состояния фонарика, сбрасываем подсчет нажатий кнопки питания
        resetTimeStamps(settings);

    }

    /**
     * Непосредственное переключение фонарика, должно вызываться только из одного места: handleFlashToggle
     */
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
                Log.d(LOG_TAG, "EXCEPTION!!!!!! ---> Vibrator");
            }
        }

        sendMessageToActivity();
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
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

            if (cmd.equalsIgnoreCase("toggle")) {
                // Переключение
                handleFlashToggle(settings);
            } else if (cmd.equalsIgnoreCase("scr_on") || cmd.equalsIgnoreCase("scr_off")) {
                // Анализ нажатия кнопки питания
                long cur = System.currentTimeMillis();
                long diff = Utils.diff3(settings.getLong("preLastClick", 0), settings.getLong("lastClick", 0), cur);
                if (diff > 0 && diff < Integer.parseInt(settings.getString("prefInterval", "2000"))) {
                    handleFlashToggle(settings);
                } else {
                    updateTimeStamps(settings, cur);
                }
            }
        }

        Log.d(LOG_TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * Событие автоотключения фонарика по таймеру
     * */
    private class timerTask extends TimerTask {
        public void run() {
            Log.d(LOG_TAG, "---> timerTask Fired! ");
            // Если фонарик светит, тогда вырубим его
            if (Utils.isFlashOn()) {
                handleFlashToggle(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
            }
        }
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
