package com.instabridge.wifiprovider

interface WifiProvider {
    fun start()
    fun stop()

    interface Callback {
        fun onCloseByUpdate(items: List<WiFi>)
        fun onFarAwayUpdate(items: List<WiFi>)
    }
}