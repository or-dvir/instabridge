package com.sample.myapplication.model

import android.support.v7.widget.RecyclerView
import android.view.View
import com.instabridge.wifiprovider.WiFi
import com.mikepenz.fastadapter.items.ModelAbstractItem
import com.sample.myapplication.R
import com.sample.myapplication.other.getWifiSignalImage
import kotlinx.android.synthetic.main.wifi_row.view.*

class WifiAdapterItem(mWifi: WiFi) : ModelAbstractItem<WiFi, WifiAdapterItem, WifiAdapterItem.ViewHolder>(mWifi)
{
    override fun getType() = R.id.fastadapter_id_wifi
    override fun getLayoutRes() = R.layout.wifi_row
    override fun getViewHolder(v: View) = ViewHolder(v)

    override fun bindView(holder: ViewHolder, payloads: MutableList<Any>)
    {
        super.bindView(holder, payloads)

        holder.view.apply {
            wifiRow_iv_signal.setImageResource(model.getWifiSignalImage())
            wifiRow_tv_wifiName.text = model.ssid

            //NOTE:
            //no need to "bind" wifiRow_iv_arrow because it is identical to all rows
        }
    }

    override fun unbindView(holder: ViewHolder)
    {
        super.unbindView(holder)

        holder.view.apply {
            wifiRow_iv_signal.setImageDrawable(null)
            wifiRow_tv_wifiName.text = ""

            //NOTE:
            //no need to "unbind" wifiRow_iv_arrow because it is identical to all rows
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////

    //even though this class is empty, we cannot directly use RecyclerView.ViewHolder
    //because it is abstract (cannot create instances of it)
    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}