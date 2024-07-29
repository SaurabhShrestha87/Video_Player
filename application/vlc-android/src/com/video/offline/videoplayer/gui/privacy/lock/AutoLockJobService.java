package com.video.offline.videoplayer.gui.privacy.lock;


import android.app.job.JobParameters;
import android.app.job.JobService;
import android.widget.Toast;

import androidx.annotation.Nullable;


public final class AutoLockJobService extends JobService {

    public AutoLockJobService() {
        super();
    }

    @Override
    public boolean onStartJob(@Nullable JobParameters params) {
        final LockStore lockStore = LockStore.getInstance(getApplicationContext());
        if (!lockStore.isLocked()) {
            lockStore.lock();
            Toast.makeText(this, "Private Folder Locked", Toast.LENGTH_LONG).show();
        }
        return false;
    }

    @Override
    public boolean onStopJob(@Nullable JobParameters params) {
        return false;
    }
}
