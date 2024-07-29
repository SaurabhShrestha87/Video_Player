package com.video.offline.videoplayer;
import com.video.offline.videoplayer.IDebugLogServiceCallback;

interface IDebugLogService
{
    void start();
    void stop();
    void clear();
    void save();
    void registerCallback(IDebugLogServiceCallback cb);
    void unregisterCallback(IDebugLogServiceCallback cb);
}

