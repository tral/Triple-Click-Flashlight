package ru.perm.trubnikov.clicklight;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class Utils {

    public static Camera cam = null;// has to be static, otherwise onDestroy() destroys it

    public static boolean flashlightToggle(Context context) {
        if (cam == null) {
            flashlightOn(context);
            return true;
        } else {
            flashlightOff(context);
            return false;
        }
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

}
