package com.sample.myapplication.other

import android.support.annotation.DrawableRes
import android.support.v7.app.AppCompatActivity
import com.instabridge.wifiprovider.WiFi
import com.instabridge.wifiprovider.interfaces_and_enums.ESignalStrength
import com.sample.myapplication.R


//a file for general stuff such as extension functions, type aliases, constants, etc...

fun AppCompatActivity.setHomeUpEnabled(enabled: Boolean) = supportActionBar?.setDisplayHomeAsUpEnabled(enabled)

//this function needs to be here (as extension function) and not in the class WiFi ("wifiprovider" module)
//because it requires access to the resources of "myapplication" module
@DrawableRes
fun WiFi.getWifiSignalImage(): Int
{
    return when (signalStrength)
    {
        ESignalStrength.NONE   -> R.drawable.ic_signal_0
        ESignalStrength.LOW    -> R.drawable.ic_signal_1
        ESignalStrength.MEDIUM -> R.drawable.ic_signal_2
        ESignalStrength.HIGH   -> R.drawable.ic_signal_3
        ESignalStrength.BEST   -> R.drawable.ic_signal_4
        //not sure if this is best type of exception to throw here...
        //the idea is to "future proof" this method - meaning that
        //if in the future we update ESignalStrength, we should not forget to
        //update this method as well (if we do forget, it will crash during testing and we'll immediately fix it)
        else                   -> throw EnumConstantNotPresentException(ESignalStrength::class.java,
                                                                        signalStrength.toString())
    }
}

/**
 * similar to [List.subList] except if [toIndex] is larger than this list, no exception is thrown
 * and the sub list returned will contain the elements between [fromIndex] (inclusive)
 * and the end of this list
 */
fun <T> List<T>.subListAlwaysInBounds(fromIndex: Int, toIndex: Int): List<T>
{
    return if(toIndex <= size)
        subList(fromIndex, toIndex)
    else
        subList(fromIndex, size)
}