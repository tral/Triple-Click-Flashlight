package ru.perm.trubnikov.clicklight;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ScreenReceiver extends BroadcastReceiver {

    private boolean screenOff;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            screenOff = true;
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            screenOff = false;
        }
        Utils.startSvc(context, screenOff ? "scr_off" : "scr_on");

        //Intent i = new Intent(context, ClickFlashService.class);
        //i.putExtra("cmd", screenOff ? "scr_off" : "scr_on");
        //context.startService(i);
    }

}
