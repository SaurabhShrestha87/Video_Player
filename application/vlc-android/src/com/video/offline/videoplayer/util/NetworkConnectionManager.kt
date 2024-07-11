package com.video.offline.videoplayer.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
object NetworkConnectionManager {
    val isMetered = MutableLiveData(true)
    val hasConnection = MutableLiveData(false)

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            if (hasConnection.value == false) hasConnection.postValue(true)

        }

        // Network capabilities have changed for the network
        override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
        ) {
            super.onCapabilitiesChanged(network, networkCapabilities)
            val metered = !networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)
            if (metered != isMetered.value) isMetered.postValue(metered)
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            if (hasConnection.value == true) hasConnection.postValue(false)
        }
    }
    fun start(context: Context) {
        val networkRequest = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build()
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.requestNetwork(networkRequest, networkCallback)
    }
}