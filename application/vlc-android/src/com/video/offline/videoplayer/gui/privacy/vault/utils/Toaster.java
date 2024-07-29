
package com.video.offline.videoplayer.gui.privacy.vault.utils;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.lang.ref.WeakReference;

public class Toaster {
    private static Toaster toaster;
    private static Toast toast;
    private final WeakReference<Context> weakReference;

    private Toaster(@NonNull Context context) {
        weakReference = new WeakReference<>(context);
    }

    public static Toaster getInstance(@NonNull Context context) {
        if (toaster == null) {
            toaster = new Toaster(context.getApplicationContext());
        }
        return toaster;
    }

    public void showShort(@NonNull String message) {
        show(message, Toast.LENGTH_SHORT);
    }

    public void showLong(@NonNull String message) {
        show(message, Toast.LENGTH_LONG);
    }

    private void show(@NonNull String message, int duration) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(weakReference.get(), message, duration);
        toast.show();
    }

}