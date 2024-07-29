package com.video.offline.videoplayer.medialibrary;

import android.util.Log;

import java.util.concurrent.atomic.AtomicBoolean;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

public class SingleEvent<T> extends MutableLiveData<T> {

    private static final String TAG = "SingleEvent";
    private final AtomicBoolean mPending = new AtomicBoolean(false);

    @Override
    public void observe(@NonNull LifecycleOwner owner, @NonNull final Observer<? super T> observer) {
        if (hasActiveObservers()) Log.w(TAG, "Multiple observers registered but only one will be notified of changes.");
        // Observe the internal MutableLiveData
        super.observe(owner, new Observer<T>() {
            @Override
            public void onChanged(T t) {
                if (mPending.compareAndSet(true, false)) observer.onChanged(t);
            }
        });
    }

    @Override
    public void observeForever(@NonNull final Observer<? super T> observer) {
        super.observeForever(new Observer<T>() {
            @Override
            public void onChanged(T t) {
                if (mPending.compareAndSet(true, false)) observer.onChanged(t);
            }
        });
    }

    @Override
    public void setValue(T value) {
        mPending.set(true);
        super.setValue(value);
    }

    public void clear() {
        super.setValue(null);
    }
}
