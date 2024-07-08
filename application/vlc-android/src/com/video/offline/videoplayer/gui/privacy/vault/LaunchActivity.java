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

package com.video.offline.videoplayer.gui.privacy.vault;

import android.content.Intent;
import android.hardware.biometrics.BiometricPrompt;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.karikari.goodpinkeypad.KeyPadListerner;

import com.video.offline.videoplayer.R;
import com.video.offline.videoplayer.databinding.ActivityLaunchBinding;
import com.video.offline.videoplayer.gui.BaseActivity;
import com.video.offline.videoplayer.gui.privacy.lock.LockStore;
import com.video.offline.videoplayer.gui.privacy.vault.encryption.Password;
import com.video.offline.videoplayer.gui.privacy.vault.utils.Settings;

import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

public class LaunchActivity extends BaseActivity {
    private static final String TAG = "LaunchActivity";
    public static long GLIDE_KEY = System.currentTimeMillis();
    public static String EXTRA_ONLY_UNLOCK = "u";
    private ActivityLaunchBinding binding;
    private Settings settings;
    private AtomicBoolean isStarting;
    private LockStore lockStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        binding = ActivityLaunchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
    }

    private void init() {
        settings = Settings.getInstance(this);
        isStarting = new AtomicBoolean(false);
        lockStore = LockStore.getInstance(this);
        Password.lock(this, settings);
        lockStore.lock();
        if (lockStore.hasPassword()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                unlockViaBiometricAuthentication();
            }
            binding.setPin.setVisibility(View.GONE);
            binding.enterPin.setVisibility(View.VISIBLE);
        } else {
            binding.setPin.setVisibility(View.VISIBLE);
            binding.enterPin.setVisibility(View.GONE);
        }
        setListeners();
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private void unlockViaBiometricAuthentication() {
        Executor executor = this.getMainExecutor();
        CancellationSignal cancellationSignal = new CancellationSignal();
        cancellationSignal.setOnCancelListener(this::finish);

        BiometricPrompt prompt = new BiometricPrompt.Builder(this).setTitle(getString(R.string.tile_unlock)).setDescription(getString(R.string.password_input_biometric_message)).setNegativeButton(getString(R.string.password_input_biometric_fallback), executor, (dialog, which) -> {
            // DO NOTHING!!
        }).build();

        prompt.authenticate(cancellationSignal, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
            }

            @Override
            public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
                super.onAuthenticationHelp(helpCode, helpString);
            }

            @Override
            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                doUnlock(null);
                super.onAuthenticationSucceeded(result);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
            }
        });
    }
    private void setListeners() {
        binding.forgot.setOnClickListener(v -> {
            lockStore.setPassword("1234");
            lockStore.setBiometricUnlockEnabled(true);
        });
        binding.key.setKeyPadListener(new KeyPadListerner() {
            @Override
            public void onKeyPadPressed(@Nullable String value) {
                if (value != null && value.length() == 4) {
                    if (lockStore.hasPassword()) {
                        if (lockStore.passwordMatch(value)) {
                            doUnlock(value);
                        } else {
                            Log.e(TAG, "onKeyPadPressed: Wrong Password!");
                            binding.key.setErrorIndicators(true);
                            binding.key.setErrorText("Wrong Password!");
                        }
                    } else {
                        if (binding.biometricCb.isChecked()) {
                            lockStore.setAutoLockEnabled(true);
                        }
                        lockStore.setPassword(value);
                        doUnlock(value);
                    }
                }
            }

            @Override
            public void onKeyBackPressed() {
                binding.key.setErrorIndicators(false);
                binding.key.setErrorText("");
            }

            @Override
            public void onClear() {

            }
        });
    }

    private void doUnlock(@Nullable String value) {
        if (isStarting.compareAndSet(false, true)) {
            if (value != null) {
                settings.setTempPassword(value.toCharArray());
            }
            startActivity(new Intent(this, GalleryActivity.class));
            isStarting.set(false);
            lockStore.lock();
            finish();
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

    @Nullable
    @Override
    public View getSnackAnchorView(boolean overAudioPlayer) {
        return null;
    }
}