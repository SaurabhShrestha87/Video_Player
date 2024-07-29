package com.video.offline.videoplayer.gui.privacy.vault;

import android.content.Intent;

import androidx.activity.result.ActivityResult;
import androidx.appcompat.app.AppCompatActivity;

import com.video.offline.videoplayer.gui.privacy.vault.utils.BetterActivityResult;


public class BaseActivity extends AppCompatActivity {
    protected final BetterActivityResult<Intent, ActivityResult> activityLauncher = BetterActivityResult.registerActivityForResult(this);
}
