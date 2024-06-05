/*****************************************************************************
 * AboutFragment.kt
 *
 * Copyright © 2018 VLC authors and VideoLAN
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston MA 02110-1301, USA.
 */

package org.videolan.vlc.gui.privacy

import android.app.Activity
import android.content.DialogInterface
import android.hardware.biometrics.BiometricPrompt
import android.os.Bundle
import android.os.CancellationSignal
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.view.ActionMode
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.karikari.goodpinkeypad.GoodPinKeyPad
import com.karikari.goodpinkeypad.KeyPadListerner
import org.videolan.tools.setGone
import org.videolan.vlc.R
import org.videolan.vlc.gui.BaseFragment
import org.videolan.vlc.gui.CleanerFragment
import org.videolan.vlc.gui.privacy.lock.LockStore
import java.util.concurrent.Executor

class PrivacyFragment : BaseFragment() {

    private val TAG = "PrivacyFragment"

    private lateinit var eTPassword: GoodPinKeyPad
    private lateinit var biometricCb: CheckBox

    private lateinit var lockStore: LockStore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_privacy, container, false)
    }

    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?) = false

    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?) = false

    override fun onDestroyActionMode(mode: ActionMode?) {}

    override fun getTitle() = if (lockStore.hasPassword()) "SET PIN"
    else "ENTER PIN"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lockStore = LockStore.getInstance(requireActivity())
        lockStore.lock()
        requireActivity().findViewById<FloatingActionButton>(R.id.fab).setGone()
        requireActivity().findViewById<View>(R.id.sliding_tabs).setGone()
        eTPassword = view.findViewById(R.id.key)
        biometricCb = view.findViewById(R.id.biometric_cb)

        if (lockStore.hasPassword()) {
            view.findViewById<LinearLayout>(R.id.set_pin).visibility = View.GONE
            view.findViewById<LinearLayout>(R.id.enter_pin).visibility = View.VISIBLE
        } else {
            view.findViewById<LinearLayout>(R.id.set_pin).visibility = View.VISIBLE
            view.findViewById<LinearLayout>(R.id.enter_pin).visibility = View.GONE
        }

        eTPassword.setKeyPadListener(object : KeyPadListerner {
            override fun onKeyPadPressed(value: String?) {
                Log.d(TAG, "Key pressed : $value")
                if (value?.count() == 4) {
                    if (lockStore.hasPassword()) {
                        if (lockStore.passwordMatch(value)) {
                            doUnlock()
                        } else {
                            Log.e(TAG, "onKeyPadPressed: Wrong Password!", )
                            eTPassword.setErrorText("Wrong Password!")
                        }
                    } else {
                        if (biometricCb.isChecked) {
                            lockStore.isBiometricUnlockEnabled = true
                        }
                        lockStore.setPassword(value)
                        doUnlock()
                    }
                }
            }

            override fun onKeyBackPressed() {

            }

            override fun onClear() {

            }
        })

        if (lockStore.isLocked && lockStore.hasPassword() && lockStore.isBiometricUnlockEnabled) {
            unlockViaBiometricAuthentication()
        }
        Log.d(TAG, "isLocked : ${lockStore.isLocked}")
        Log.d(TAG, "hasPassword : ${lockStore.hasPassword()}")
    }

    private fun goToNextPage() {
        Toast.makeText(requireContext(), "TODO: GO NEXT PAGE!", Toast.LENGTH_SHORT).show()
        requireActivity().supportFragmentManager.beginTransaction()
            .add(R.id.fragment_placeholder, CleanerFragment())
            .commit()
    }

    @RequiresApi(29)
    private fun unlockViaBiometricAuthentication() {
        val executor: Executor = requireActivity().mainExecutor
        val cancellationSignal = CancellationSignal()
        cancellationSignal.setOnCancelListener { requireActivity().finish() }

        val prompt =
            BiometricPrompt.Builder(requireContext()).setTitle(getString(R.string.tile_unlock))
                .setDescription(getString(R.string.password_input_biometric_message))
                .setNegativeButton(
                    getString(R.string.password_input_biometric_fallback), executor
                ) { dialog: DialogInterface?, which: Int ->
                    {
                        // do nothing but could show UI to user now instead of start...
                    }
                }.build()
        prompt.authenticate(
            cancellationSignal,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    doUnlock()
                }

                override fun onAuthenticationFailed() {
                    Toast.makeText(requireContext(), "Fingerprint mismatch!", Toast.LENGTH_SHORT).show()
                    requireActivity().setResult(Activity.RESULT_CANCELED)
                    requireActivity().finish()
                }
            })
    }

    private fun doUnlock() {
        Log.i(TAG, "doUnlock: RAN!")
        lockStore.unlock()
        goToNextPage()
    }
}
