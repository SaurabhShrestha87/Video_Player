package com.video.offline.videoplayer.medialibrary;

import android.content.Context;

public final class MLContextTools {
    private static MLContextTools sInstance;
    private Context mContext;

    public void setContext(Context context) {
        this.mContext = context.getApplicationContext();
    }

    public static MLContextTools getInstance() {
        if (sInstance == null) {
            sInstance = new MLContextTools();
        }
        return sInstance;
    }

    public Context getContext() {
        return mContext;
    }
}