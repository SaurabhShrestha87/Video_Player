package com.video.offline.videoplayer.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * View model allowing to emit / collect display setting changes between
 * a calling fragment and the [DisplaySettingsDialog]
 *
 */
class DisplaySettingsViewModel: ViewModel() {
    /**
     * Display setting object
     * Initial values should always be discarded
     *
     * @property key the setting key
     * @property value the setting value
     */
    data class SettingChange(
            val key: String = "init",
            val value: Any = 1,
    )

    private val _settingChangeFlow = MutableStateFlow(SettingChange())
    val settingChangeFlow = _settingChangeFlow.asStateFlow()

    /**
     * Send a new event when a setting is changed
     *
     * @param key the setting key
     * @param value the setting value
     */
    suspend fun send(key: String, value: Any) {
        _settingChangeFlow.emit(SettingChange(key, value))
    }

    /**
     * When the flow value is consumed, revert to "init" state
     *
     */
    suspend fun consume() {
        _settingChangeFlow.emit(SettingChange())
    }

}