package com.sample.myapplication.vvm

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import com.instabridge.wifiprovider.FakeWiFiProvider
import com.instabridge.wifiprovider.WiFi
import com.instabridge.wifiprovider.WifiProvider

class ActivityMainViewModel(app: Application) : AndroidViewModel(app)
{
    val mCurrentWifi_close = MutableLiveData<List<WiFi>>()
    val mCurrentWifi_far = MutableLiveData<List<WiFi>>()

    private val mProvider =
        FakeWiFiProvider(object : WifiProvider.Callback
                         {
                             override fun onCloseByUpdate(items: List<WiFi>) = updateWifis(true, items)
                             override fun onFarAwayUpdate(items: List<WiFi>) = updateWifis(false, items)
                         })

    fun startProvider() = mProvider.start()
    fun stopProvider() = mProvider.stop()

    fun triggerFurtherAwayObserver()
    {
        mCurrentWifi_far.value = mCurrentWifi_far.value
    }

    private fun updateWifis(close: Boolean, items: List<WiFi>)
    {
        //options for choosing which wifis to display and in what order:
        //1. closest wifi first.
        //2. strongest signal first (assuming that even if they are further away, they're not too far).
        //3. write a short algorithm that combines distance and signal strength to an overall score
        //   and sort by that.
        //4. let the user choose (in app settings) which wifis they'd like to display (closest/strongest signal/combined).
        //
        //for simplicity i will use option 1. i will do this by combining all available wifis (new and existing),
        //sorting them by distance, and then sending the result to the activity (observer).
        //in some cases this could lead to unnecessarily UI updates
        //but for the purposes of this assignment (and simplicity) it should be good enough.

        //if list is empty - do nothing
        if(items.isEmpty())
            return

        val allWifis = mutableListOf<WiFi>()
        allWifis.addAll(items)

        allWifis.addAll(
                //first time the lists will be null
                if (close)
                    mCurrentWifi_close.value.orEmpty()
                else
                    mCurrentWifi_far.value.orEmpty()
        )

        allWifis.apply {
            sortBy { it.distanceMeter }

            //note:
            //actually displaying only the top 3 is done in the activity
            if (close)
                mCurrentWifi_close.value = this
            else
                mCurrentWifi_far.value = this
        }
    }
}