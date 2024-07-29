package com.video.offline.videoplayer.gui.privacy.vault.encryption;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;

import com.video.offline.videoplayer.gui.privacy.vault.LaunchActivity;
import com.video.offline.videoplayer.gui.privacy.vault.utils.FileStuff;
import com.video.offline.videoplayer.gui.privacy.vault.utils.Settings;


public class Password {
    private static final String TAG = "Password";

    public static void lock(Context context, @NonNull Settings settings) {
        Log.d(TAG, "lock");
        settings.clearTempPassword();
        FileStuff.deleteCache(context);
        Glide.get(context).clearMemory();
        //new Thread(() -> Glide.get(context).clearDiskCache()).start();
        LaunchActivity.GLIDE_KEY = System.currentTimeMillis();
    }
}
