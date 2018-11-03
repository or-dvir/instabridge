package com.sample.myapplication.vvm

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import com.instabridge.wifiprovider.WiFi
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IInterceptor
import com.mikepenz.fastadapter.adapters.ModelAdapter
import com.sample.myapplication.R
import com.sample.myapplication.model.WifiAdapterItem
import com.sample.myapplication.other.getWifiSignalImage
import com.sample.myapplication.other.subListAlwaysInBounds
import icepick.Icepick
import icepick.State
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.wifi_row.view.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.toast

class ActivityMain : AppCompatActivity(), View.OnClickListener
{
    companion object
    {
        const val TAG = "ActivityMain"
        const val EXTRA_WIFI = "EXTRA_WIFI"
        private const val BUTTON_COOLDOWN_MILLIS = 1000L
    }

    //assumptions for this assignment:
    //1) i am allowed to make changes to the given library
    //2) support for right-to-left layouts are not required
    //3) once provided, a wifi will always remain in range and will only disappear from
    //   the list if another wifi replaces it (for example because it is closer)
    //4) the device wifi is enabled and will remain enabled for as long as the app is running
    //5) once provided, the distance of a wifi network does not change (app does not actively track user movement).
    //6) all wifis given by FakeWiFiProvider are unique (no duplicates)
    //7) support for very small or very large screens (tablets) are not not required
    //8) once provided, the signal strength of a wifi does not change


    //NOTE 1:
    //in some places in this app i am accessing/setting list items by manually accessing the list indices.
    //i am aware that this isn't considered good coding - it's error prone, not scalable, and would be tedious to maintain
    //in the future when changes are made.
    //however for the purposes of this assignment (and simplicity), it's good enough.

    //NOTE 2:
    //the instructions about the "further away" group were a little confusing to me.
    //i could interpret them in 2 different ways:
    //1)   the list can only ever visually display a maximum of 3 items.
    //1.1) also, the number of items allowed in the list is limited - it starts at 3, and pressing
    //     "show more" allows more items in the list. for example pressing it once now allows a total of
    //     6 items in the list, and pressing it again allows 9 items
    //     (all while the height of the RecyclerView is no more than 3 items).
    //2)   the list always contains all available wifis (user can scroll through all of them),
    //     but it is limited in how many rows it can visually show at any given time
    //    (a number which increases when clicking "show more").
    //     for example FakeWifiProvider immediately gives us 100 wifis and we add them all to a RecyclerView
    //     of initial height 3 (as in 3 rows tall). now we click "show more" and the height of the RecyclerView
    //     changes to 6 items tall, press it again and now the RecyclerView is 9 items tall
    //    (all while the number of items in the list keeps growing as we get more wifis from FakeWifiProvider).
    //
    //i only thought about option 2 after already completing the requirements of option 1,
    //and also option 2 doesn't seem right because at some point (after clicking "show more" a few times)
    //the RecyclerView would be outside of the screen which means we would have rows in memory which the user
    //cannot even see (which negates the idea of a RecyclerView).
    //what i'm trying to say is:
    //    if(option1.isCorrect())
    //        toast("yay!") //bad code! should use R.string.yay
    //    else
    //        InstaBridge.getInstance().forgive(Or)


    //NOTE 3:
    //Jakob has my contact information. for any questions feel free to contact me by email or phone :)

    private lateinit var mViewModel: ActivityMainViewModel
    private lateinit var mAdapterRv: FastAdapter<WifiAdapterItem>
    private lateinit var mAdapterWifis: ModelAdapter<WiFi, WifiAdapterItem>

    @JvmField @State
    var mNumWifisAllowedInList: Int = 3
    @JvmField @State
    var mLastButtonClickMillis = System.currentTimeMillis()

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Icepick.restoreInstanceState(this, savedInstanceState)
        mViewModel = ViewModelProviders.of(this)
                .get(ActivityMainViewModel::class.java)

        //moved to separate functions so there isn't too much code in onCreate
        initializeObservers()
        initializeAdapter()

        activityMain_wifiClose_1.setOnClickListener(this)
        activityMain_wifiClose_2.setOnClickListener(this)
        activityMain_wifiClose_3.setOnClickListener(this)

        activityMain_rv.apply {
            addItemDecoration(DividerItemDecoration(this@ActivityMain, DividerItemDecoration.VERTICAL))
            layoutManager = LinearLayoutManager(this@ActivityMain, RecyclerView.VERTICAL, false)
            adapter = mAdapterRv
        }

        activityMain_btn_showMore.setOnClickListener{
            val now = System.currentTimeMillis()

            //if it's been less than BUTTON_COOLDOWN_MILLIS since the last click - do nothing.
            //this is to prevent accidental double-clicking the button
            if(now - mLastButtonClickMillis < BUTTON_COOLDOWN_MILLIS)
                return@setOnClickListener

            mLastButtonClickMillis = now
            mViewModel.mCurrentWifi_far.value?.let {
                mNumWifisAllowedInList += 3

                //NOTE:
                //i chose to use triggerFurtherAwayObserver()
                //because the logic is already written in the observer
                //and reusing it is good practice, saves time, and helps prevent future bugs (only 1 place to update).
                //however because i also chose to sort the wifis by distance, the entire list will be reloaded
                //when pressing the button and this may not look good for the user.
                //in production we would have to consider the apps' requirements and decide whether this is
                //acceptable in terms of user experience.
                //for the purposes of this assignment, it should be good enough.
                mViewModel.triggerFurtherAwayObserver()
                toast(String.format(getString(R.string.showingMaxWifis), mNumWifisAllowedInList))
            }
        }
    }

    private fun initializeAdapter()
    {
        mAdapterWifis = ModelAdapter(IInterceptor { WifiAdapterItem(it) })

        mAdapterRv = FastAdapter<WifiAdapterItem>().addAdapter(0, mAdapterWifis)
                .withSelectable(false)
                .withSelectOnLongClick(false)
                .withOnClickListener { v, adapter, item, position ->
                    startDetailsActivity(item.model)
                    true
                }
    }

    override fun onClick(v: View)
    {
        val index = when(v.id)
        {
            //list indices are 0-based!
            R.id.activityMain_wifiClose_1 -> 0
            R.id.activityMain_wifiClose_2 -> 1
            R.id.activityMain_wifiClose_3 -> 2
            else                          -> -1
        }

        //if for some reason we get -1, do nothing (could also show error message)
        if(index == -1)
            return

        //only open the next activity if we have a wifi in the clicked row.
        //note:
        //passing data as intent extras is not necessarily the best way.
        //here are a few examples why:
        //1) if the WiFi class grows substantially in the future
        //   it may reach the size limit for such transactions.
        //2) serialization is considered relatively slow (as opposed to parcelable for example)
        //3) this method required the object passed to be serializable (obviously).
        //despite these limitations, for the purposes of this assignment (and simplicity) it is good enough.
        getCloseWifiByIndex(index)?.let { wifi ->
            startDetailsActivity(wifi)
        }
    }

    private fun startDetailsActivity(wifi: WiFi) =
            startActivity(intentFor<ActivityWifiDetails>(EXTRA_WIFI to wifi))

    private fun getCloseWifiByIndex(index: Int): WiFi?
    {
        //while throwing an exception is considered an "expansive" operation,
        //in this particular case it is cleaner, shorter, and less error-prone code
        //as opposed to manually checking the given parameter.
        //in a production app we may prefer performance over clean code (depending on requirements),
        //but for the purposes of this assignment i prefer cleaner code (also the user will not even notice
        //the difference in performance).
        //if however we would prefer performance over clean code,
        //the manual checks would include:
        //1) making sure parameter 'index' is in the right range (0-2).
        //2) making sure the list of the viewModel is not null.
        //3) making sure that 'index' is less than the size of the list in our viewModel

        return try
        {
            mViewModel.mCurrentWifi_close.value?.get(index)
        }

        //should really only get IndexOutOfBoundsException but this is just to be safe
        catch (e: Exception)
        {
            Log.e(TAG, e.message, e)
            null
        }
    }

    private fun initializeObservers()
    {
        mViewModel.mCurrentWifi_close.observe(this, Observer {

            //if for some reason we get null, do nothing
            if(it == null)
                return@Observer

            //could be replaced with "it.isNotEmpty()"
            //but kept like this so it's consistent with the other 'if' conditions
            if (it.size >= 1)
            {
                activityMain_wifiClose_1.apply {
                    wifiRow_iv_signal.setImageResource(it[0].getWifiSignalImage())
                    wifiRow_tv_wifiName.text = it[0].ssid
                }
            }
            if (it.size >= 2)
            {
                activityMain_wifiClose_2.apply {
                    wifiRow_iv_signal.setImageResource(it[1].getWifiSignalImage())
                    wifiRow_tv_wifiName.text = it[1].ssid
                }
            }
            if (it.size >= 3)
            {
                activityMain_wifiClose_3.apply {
                    wifiRow_iv_signal.setImageResource(it[2].getWifiSignalImage())
                    wifiRow_tv_wifiName.text = it[2].ssid
                }
            }
        })

        ////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////

        mViewModel.mCurrentWifi_far.observe(this, Observer { observerList ->

            //if for some reason we get null, do nothing
            if(observerList == null)
                return@Observer

            activityMain_rv.apply {

                //reset the adapter (order and/or number of items is expected to change
                mAdapterWifis.clear()

                //once we have at least 3 items in our list, set the size of the recycler view
                //so we only show 3 items at any given time, and enable the "show more" button
                if (observerList.size >= 3 && !hasFixedSize())
                {
                        //could also use "newList.subList(0,3)" but that would create a new list object
                        //and that is a waste of runtime and memory
                        mAdapterWifis.add(observerList.component1(), observerList.component2(), observerList.component3())

                        //make sure activityMain_rv has been measured
                        post {
                            setHasFixedSize(true)
                            activityMain_btn_showMore.isEnabled = true
                        }
                }

                //less than 3 items or RecyclerView already has fixed size
                else
                {
                    //if less than 3 items, add all.
                    if(observerList.size < 3)
                        mAdapterWifis.add(observerList)
                    //if more than 3 items, add the first mNumWifisAllowedInList
                    else
                        mAdapterWifis.add(observerList.subListAlwaysInBounds(0, mNumWifisAllowedInList))
                }
            }
        })
    }

    //starting to retrieve data on onStart() and stop in onStop()
    //to prevent data being consumed while app in not in foreground.
    //in production we could keep updating in the background so the latest data is immediately available
    //when the app returns to the foreground
    override fun onStart()
    {
        super.onStart()

        //NOTE:
        //in a real app we cannot scan for networks if the devices' wifi is disabled.
        //however properly monitoring the devices' wifi connectivity state during the apps' lifecycle requires a
        //broadcast receiver.
        //for simplicity, i assume that the wifi is enabled and will remain enabled
        //for as long as the app is running
        mViewModel.startProvider()
    }

    override fun onStop()
    {
        mViewModel.stopProvider()
        super.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle?)
    {
        super.onSaveInstanceState(outState)
        Icepick.saveInstanceState(this, outState)
    }
}
