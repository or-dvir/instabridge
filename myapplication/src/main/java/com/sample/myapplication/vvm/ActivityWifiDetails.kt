package com.sample.myapplication.vvm

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.instabridge.wifiprovider.WiFi
import com.instabridge.wifiprovider.interfaces_and_enums.ESignalStrength
import com.sample.myapplication.R
import com.sample.myapplication.other.setHomeUpEnabled
import icepick.Icepick
import icepick.State
import kotlinx.android.synthetic.main.activity_wifi_details.*
import org.jetbrains.anko.Bold
import org.jetbrains.anko.append
import org.jetbrains.anko.buildSpanned
import java.util.*

class ActivityWifiDetails : AppCompatActivity()
{
    //IcePick library cannot be used with "lateinit" so
    //must initialize field here with dummy data.
    //NOTE:
    //in a more complex app this variable should be held by the appropriate ViewModel
    //class for this activity, however it seems a little silly to create a ViewModel
    //just to hold a single variable... so instead i just place it here and save the state
    @JvmField @State
    var mWifi = WiFi("", ESignalStrength.BEST, 12)

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wifi_details)
        Icepick.restoreInstanceState(this, savedInstanceState)
        setTitle(R.string.wifiDetails_title)
        setHomeUpEnabled(true)

        mWifi = intent.getSerializableExtra(ActivityMain.EXTRA_WIFI) as WiFi

        //moved to a separate function as to not have too much code in onCreate
        populateData()
    }

    private fun populateData()
    {
        //making the row title (e.g. "full name") bold for a slightly better look (in my opinion).
        //this may seem like a complicated way of doing it, but in my experience i have found
        //it to be the most convenient. here are a few reasons why:
        //1) making dynamic changes keeps the texts in strings.xml generic and i can use them in other
        //   places in the code (if needed).
        //2) using html tags (such as <b>) don't always work and Html.fromHtml() has some bugs
        //   in some versions of android.
        //3) buildSpanned allows for much greater flexibility.
        //4) buildSpanned is easy to understand when reading and is more comfortable to write than
        //   other methods (at least for me).

        activityWifiDetails_tv_fullMame.text =
                buildSpanned {
                    append(getString(R.string.wifiDetails_fullName), Bold)
                    append(mWifi.ssid)
                }

        activityWifiDetails_tv_owner.text =
                buildSpanned {
                    append(getString(R.string.wifiDetails_owner), Bold)
                    append(mWifi.owner)
                }

        activityWifiDetails_tv_provider.text =
                buildSpanned {
                    append(getString(R.string.wifiDetails_provider), Bold)
                    append(mWifi.provider)
                }

        activityWifiDetails_tv_ipAddress.text =
                buildSpanned {
                    append(getString(R.string.wifiDetails_ipAddress), Bold)
                    append(mWifi.ip)
                }

        activityWifiDetails_tv_signalStrength.text =
                buildSpanned {
                    append(getString(R.string.wifiDetails_signalStrength), Bold)
                    append(mWifi.signalStrength.toString())
                }

        activityWifiDetails_tv_distance.text =
                buildSpanned {
                    append(getString(R.string.wifiDetails_distance), Bold)
                    append(mWifi.distanceMeter.toString())
                }

        activityWifiDetails_tv_connectionSpeed.text =
                buildSpanned {
                    append(getString(R.string.wifiDetails_connectionSpeed), Bold)
                    append(mWifi.speedMbps.toString())
                }

        activityWifiDetails_tv_security.text =
                buildSpanned {
                    append(getString(R.string.wifiDetails_security), Bold)
                    append(mWifi.security)
                }

        activityWifiDetails_tv_status.text =
                buildSpanned {
                    append(getString(R.string.wifiDetails_status), Bold)
                    append(getRandomStatus())
                }
    }

    private fun getRandomStatus(): String
    {
        //NOTES:
        //1) this method will be called every time the activity is created (including orientation changes)
        //   and that may look confusing to the user. however it actually makes sense because
        //   the status of a wifi network is dynamic (could be connected one moment, and not connected the next).
        //2) the fact that this is generated randomly may cause a situation where the device is "connected"
        //   to multiple networks at the same time. consider

        val num = Random().nextInt(4)

        return when (num)
        {
            0 -> getString(R.string.wifiStatus_connected)
            1 -> getString(R.string.wifiStatus_notConnected)
            2 -> getString(R.string.wifiStatus_connectedNoInternetAccess)
            3 -> getString(R.string.wifiStatus_connectedSignInRequired)
            //should never get to "else", just making compiler happy.
            //in a production app we can throw an exception here to prevent future bugs
            else -> ""
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        return if (item.itemId == android.R.id.home)
        {
            onBackPressed()
            true
        }
        else super.onOptionsItemSelected(item)
    }

    override fun onSaveInstanceState(outState: Bundle?)
    {
        super.onSaveInstanceState(outState)
        Icepick.saveInstanceState(this, outState)
    }
}
