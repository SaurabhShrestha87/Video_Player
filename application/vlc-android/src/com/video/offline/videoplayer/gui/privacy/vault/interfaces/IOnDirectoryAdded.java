package com.video.offline.videoplayer.gui.privacy.vault.interfaces;

import android.net.Uri;

import androidx.annotation.NonNull;

public interface IOnDirectoryAdded {
    void onAddedAsRoot();

    void onAddedAsChildOf(@NonNull Uri parentUri);

    void onAlreadyExists(boolean isRootDir);
}
