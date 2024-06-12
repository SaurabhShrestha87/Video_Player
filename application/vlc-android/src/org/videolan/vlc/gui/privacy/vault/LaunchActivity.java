/*
 * Valv-Android
 * Copyright (C) 2023 Arctosoft AB
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses/.
 */

package org.videolan.vlc.gui.privacy.vault;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.karikari.goodpinkeypad.KeyPadListerner;

import org.videolan.vlc.databinding.ActivityLaunchBinding;
import org.videolan.vlc.gui.privacy.vault.encryption.Password;
import org.videolan.vlc.gui.privacy.vault.utils.Settings;

import java.util.concurrent.atomic.AtomicBoolean;


public class LaunchActivity extends AppCompatActivity {
    private static final String TAG = "LaunchActivity";
    public static long GLIDE_KEY = System.currentTimeMillis();
    public static String EXTRA_ONLY_UNLOCK = "u";
    private ActivityLaunchBinding binding;
    private Settings settings;
    private AtomicBoolean isStarting;
    private boolean onlyUnlock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        binding = ActivityLaunchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        onlyUnlock = getIntent().getBooleanExtra(EXTRA_ONLY_UNLOCK, false);
        init();
    }

    private void init() {
        settings = Settings.getInstance(this);
        isStarting = new AtomicBoolean(false);
        if (!onlyUnlock) {
            Password.lock(this, settings);
        }

        setListeners();
    }

    private void setListeners() {
        binding.key.setKeyPadListener(new KeyPadListerner() {
            @Override
            public void onKeyPadPressed(@Nullable String value) {
                if (value != null && value.length() == 4) {
                    doUnlock(value);
                }
            }

            @Override
            public void onKeyBackPressed() {

            }

            @Override
            public void onClear() {

            }
        });
    }

    private void doUnlock(String value) {
        if (isStarting.compareAndSet(false, true)) {
            settings.setTempPassword(value.toCharArray());
            if (onlyUnlock) {
                finish();
            } else {
                startActivity(new Intent(this, GalleryActivity.class));
                isStarting.set(false);
            }
        }
    }

    @Override
    protected void onResume() {
        GLIDE_KEY = System.currentTimeMillis();
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: ");
        Password.lock(this, settings);
        super.onBackPressed();
    }
}