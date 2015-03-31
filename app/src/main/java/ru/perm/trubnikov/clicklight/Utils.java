package ru.perm.trubnikov.clicklight;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.view.Gravity;
import android.widget.Toast;

public class Utils {

    public static Camera cam = null;// has to be static, otherwise onDestroy() destroys it
    public static final int NOTIFICATION_ID = 1;

    public static boolean flashlightToggle(Context context) {
        if (cam == null) {
            flashlightOn(context);
            //sendNotification(context);
            return true;
        } else {
            flashlightOff(context);
            return false;
        }
    }

    public static Notification getNotification(Context context) {
        Intent intent = new Intent(context, NotifyActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        builder.setSmallIcon(R.drawable.ic_notify);

        builder.setContentIntent(pendingIntent);

        builder.setAutoCancel(true);

        if (Build.VERSION.SDK_INT > 13) {
            builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher));
        }

        builder.setContentTitle(context.getResources().getString(R.string.notify_title));
        builder.setContentText(context.getResources().getString(R.string.notify_desc1));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            builder.setSubText(context.getResources().getString(R.string.notify_desc2));
        }

        return builder.build();
    }

    public static boolean isFlashOn() {
        return !(cam == null);
    }

    public static void flashlightOn(Context context) {
        try {
            if (context.getPackageManager().hasSystemFeature(
                    PackageManager.FEATURE_CAMERA_FLASH)) {

                cam = Camera.open();
                Camera.Parameters p = cam.getParameters();
                p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                cam.setParameters(p);

                Utils.setPreviewTexture();

                cam.startPreview();

            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, context.getString(R.string.error_flash_on), Toast.LENGTH_SHORT).show();
        }
    }

    public static void flashlightOff(Context context) {
        try {
            if (context.getPackageManager().hasSystemFeature(
                    PackageManager.FEATURE_CAMERA_FLASH)) {
                cam.stopPreview();
                cam.release();
                cam = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, context.getString(R.string.error_flash_off),
                    Toast.LENGTH_SHORT).show();
        }

        try {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
            notificationManager.cancel(NOTIFICATION_ID);
        } catch (Exception e) {
        }

    }

    public static void setPreviewTexture() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            // We only need it for Lollipop
            setPreviewTextureHoneycomb();
        }
    }

    @TargetApi(11)
    public static void setPreviewTextureHoneycomb() {
        try {
            cam.setPreviewTexture(new SurfaceTexture(0));
        } catch (Exception e) {
        }
    }

    public static long diff3(long p, long l, long c) {
        if (p < 1 || l < 1 || c < 1) {
            return -1;
        }

        return (c - p);
    }

    public static void ShowToast(Context context, int txt, int lng) {
        Toast toast = Toast.makeText(context, txt, lng);
        toast.setGravity(Gravity.TOP, 0, 0);
        toast.show();
    }

    protected static void startSvc(Context c, String cmd) {
        Intent intent = new Intent(c, ClickFlashService.class);
        Bundle b = new Bundle();
        b.putString("cmd", cmd);
        intent.putExtras(b);
        c.startService(intent);

    }

    // Android API to check if call is Active or On Hold
    public static boolean isCallActive(Context context){
        AudioManager manager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        if(manager.getMode()==AudioManager.MODE_IN_CALL){
            return true;
        }
        else{
            return false;
        }
    }

}
