
package com.video.offline.videoplayer.gui.privacy.vault.utils;

import androidx.annotation.NonNull;

import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;

import com.video.offline.videoplayer.gui.privacy.vault.LaunchActivity;


public class GlideStuff {

    @NonNull
    public static RequestOptions getRequestOptions() {
        return new RequestOptions()
                .signature(new ObjectKey(LaunchActivity.GLIDE_KEY));
    }

}
