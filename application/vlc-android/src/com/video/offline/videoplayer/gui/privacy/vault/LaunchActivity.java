package com.video.offline.videoplayer.gui.privacy.vault;

import android.content.Intent;
import android.hardware.biometrics.BiometricPrompt;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

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
    Boolean isReset = false;
    private ActivityLaunchBinding binding;
    private Settings settings;
    private AtomicBoolean isStarting;
    private LockStore lockStore;

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        binding = ActivityLaunchBinding.inflate(getLayoutInflater());
        isReset = getIntent().getBooleanExtra("reset", false);
        setContentView(binding.getRoot());
        init();
    }

    private void initEmail() {
        binding.pinLytMain.setVisibility(View.GONE);
        binding.emailLytMain.setVisibility(View.VISIBLE);
        binding.emailEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    binding.clearBtn.setVisibility(View.VISIBLE);
                    binding.clearBtn.setOnClickListener(v -> {
                        binding.emailEt.getText().clear();
                        binding.clearBtn.setVisibility(View.GONE);
                    });
                }
            }
        });
    }

    private void init() {
        settings = Settings.getInstance(this);
        isStarting = new AtomicBoolean(false);
        lockStore = LockStore.getInstance(this);
        Password.lock(this, settings);
        lockStore.lock();
        if (lockStore.hasPassword() && isReset) {
            binding.setPin.setVisibility(View.VISIBLE);
            binding.enterPin.setVisibility(View.GONE);
            binding.emailLytMain.setVisibility(View.GONE);
        } else if (lockStore.hasPassword()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && lockStore.isBiometricUnlockEnabled()) {
                unlockViaBiometricAuthentication();
            }
            binding.setPin.setVisibility(View.GONE);
            binding.enterPin.setVisibility(View.VISIBLE);
            binding.emailLytMain.setVisibility(View.GONE);
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
                Log.e(TAG, "onAuthenticationError: error code : " + errorCode + " errString: " + errString);
                super.onAuthenticationError(errorCode, errString);
            }

            @Override
            public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
                Log.e(TAG, "onAuthenticationHelp: help code : " + helpCode + " Help string: " + helpString);
                super.onAuthenticationHelp(helpCode, helpString);
            }

            @Override
            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                Log.e(TAG, "onAuthenticationSucceeded: " + result.toString());
                doUnlock(null);
                super.onAuthenticationSucceeded(result);
            }

            @Override
            public void onAuthenticationFailed() {
                Log.e(TAG, "onAuthenticationFailed: ");
                super.onAuthenticationFailed();
            }
        });
    }

    private void setListeners() {
        binding.forgot.setOnClickListener(v -> {
            Toast.makeText(this, "TODO: Send email to reset password?", Toast.LENGTH_SHORT).show();
        });
        binding.key.setKeyPadListener(new KeyPadListerner() {
            @Override
            public void onKeyPadPressed(@Nullable String value) {
                if (value != null && value.length() == 4) {
                    if (lockStore.hasPassword() && isReset) {
                        if (binding.biometricCb.isChecked()) {
                            lockStore.setBiometricUnlockEnabled(true);
                        }
                        lockStore.setPassword(value);
                        doUnlock(value);
                    } else if (lockStore.hasPassword()) {
                        if (lockStore.passwordMatch(value)) {
                            doUnlock(value);
                        } else {
                            Log.e(TAG, "onKeyPadPressed: Wrong Password!");
                            binding.key.setErrorIndicators(true);
                            binding.key.setErrorText("Wrong Password!");
                        }
                    } else {
                        getEmail(value, binding.biometricCb.isChecked());
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

    private void getEmail(String password, boolean isBiometricUnlockEnabled) {
        initEmail();
        binding.close.setOnClickListener(v -> {
            binding.pinLytMain.setVisibility(View.VISIBLE);
            binding.emailLytMain.setVisibility(View.GONE);
        });
        binding.next.setOnClickListener(v -> {
            //check for email formatting.
            String email = binding.emailEt.getText().toString();
            if (!isValidEmail(email)) {
                binding.emailEt.setError("Invalid Email");
                return;
            }
            if (isBiometricUnlockEnabled) {
                lockStore.setBiometricUnlockEnabled(true);
            }
            lockStore.setPassword(password);
            lockStore.setEmail(email);
            doUnlock(password);
        });
    }

    private void doUnlock(@Nullable String value) {
        if (isStarting.compareAndSet(false, true)) {
            if (value != null) {
                settings.setTempPassword(value.toCharArray());
            } else {
                settings.setTempPassword("1234".toCharArray());
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