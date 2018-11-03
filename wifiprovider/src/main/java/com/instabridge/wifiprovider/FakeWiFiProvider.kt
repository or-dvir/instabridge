package com.instabridge.wifiprovider

import android.os.CountDownTimer
import java.util.*
import java.util.concurrent.TimeUnit

class FakeWiFiProvider(private val callback: WifiProvider.Callback) : WifiProvider
{

    private val countDownTimer = object : CountDownTimer(Long.MAX_VALUE,
                                                         TimeUnit.SECONDS.toMillis(5))
    {
        override fun onFinish() = cancel()

        override fun onTick(p0: Long)
        {
            val random = Random()
            if (random.nextBoolean())
                callback.onCloseByUpdate(generateFakeWiFis(random.nextInt(10)))
            else
                callback.onFarAwayUpdate(generateFakeWiFis(random.nextInt(10)))
        }
    }

    override fun stop() = countDownTimer.onFinish()

    override fun start()
    {
        countDownTimer.start()
    }

    private fun generateFakeWiFis(numberOfWifis: Int): List<WiFi>
    {
        val list = mutableListOf<WiFi>()

        for (i in 1..numberOfWifis)
        {
            list.add(WiFi(UUID.randomUUID().toString()))
        }

        return list
    }
}