package com.digitalchotu.cheerup

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

class NetworkManager(private val connectivityManager: ConnectivityManager?) {
    fun isNetworkAvailable(): Boolean {
        val network = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            connectivityManager?.activeNetwork
        } else {
            val connectivityManager = connectivityManager as ConnectivityManager
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnectedOrConnecting
        }
        val capabilities = connectivityManager?.getNetworkCapabilities(network)
        return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}