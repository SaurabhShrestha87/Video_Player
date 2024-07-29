package com.video.offline.videoplayer;

interface IDebugLogServiceCallback
{
    void onStarted(in List<String> logList);
    void onStopped();
    void onLog(String msg);
    void onSaved(boolean success, String path);
}
