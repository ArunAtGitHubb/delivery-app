package com.jellysoft.deliveryapp.util

import androidx.annotation.RequiresPermission
import android.Manifest.permission
import android.content.Context
import android.graphics.Point
import android.net.NetworkInfo
import android.net.ConnectivityManager
import com.jellysoft.deliveryapp.util.NetworkUtils
import android.telephony.TelephonyManager
import android.view.WindowManager
import java.lang.UnsupportedOperationException
import java.util.regex.Pattern

/**
 * The type Network utils.
 *
 * @author Arun
 */
class NetworkUtils private constructor() {
    init {
        throw UnsupportedOperationException(
            "Should not create instance of Util class. Please use as static.."
        )
    }

    companion object {
        /**
         * Get the network info
         *
         * @param context the context
         * @return network info
         */
        @RequiresPermission(permission.ACCESS_NETWORK_STATE)
        private fun getNetworkInfo(context: Context): NetworkInfo? {
            val cm = (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
            return cm.activeNetworkInfo
        }

        /**
         * Check if there is any connectivity
         *
         * @param context the context
         * @return boolean boolean
         */
        @RequiresPermission(permission.ACCESS_NETWORK_STATE)
        fun isConnected(context: Context): Boolean {
            val info = getNetworkInfo(context)
            return info != null && info.isConnected
        }

        /**
         * Check if there is fast connectivity
         *
         * @param context the context
         * @return boolean boolean
         */
        @RequiresPermission(permission.ACCESS_NETWORK_STATE)
        fun isConnectedFast(context: Context): Boolean {
            val info = getNetworkInfo(context)
            return info != null && info.isConnected && isConnectionFast(
                info.type,
                info.subtype
            )
        }

        /**
         * Check if there is any connectivity to a mobile network
         *
         * @param context the context
         * @return boolean boolean
         */
        @RequiresPermission(permission.ACCESS_NETWORK_STATE)
        fun isConnectedMobile(context: Context): Boolean {
            val info = getNetworkInfo(context)
            return (info != null && info.isConnected
                    && info.type == ConnectivityManager.TYPE_MOBILE)
        }

        /**
         * Check if there is any connectivity to a Wifi network
         *
         * @param context the context
         * @return boolean boolean
         */
        @RequiresPermission(permission.ACCESS_NETWORK_STATE)
        fun isConnectedWifi(context: Context): Boolean {
            val info = getNetworkInfo(context)
            return info != null && info.isConnected && info.type == ConnectivityManager.TYPE_WIFI
        }

        /**
         * Check if the connection is fast
         *
         * @param type    the type
         * @param subType the sub type
         * @return boolean boolean
         */
        private fun isConnectionFast(type: Int, subType: Int): Boolean {
            return if (type == ConnectivityManager.TYPE_WIFI) {
                true
            } else if (type == ConnectivityManager.TYPE_MOBILE) {
                when (subType) {
                    TelephonyManager.NETWORK_TYPE_1xRTT -> false // ~ 50-100 kbps
                    TelephonyManager.NETWORK_TYPE_CDMA -> false // ~ 14-64 kbps
                    TelephonyManager.NETWORK_TYPE_EDGE -> false // ~ 50-100 kbps
                    TelephonyManager.NETWORK_TYPE_EVDO_0 -> true // ~ 400-1000 kbps
                    TelephonyManager.NETWORK_TYPE_EVDO_A -> true // ~ 600-1400 kbps
                    TelephonyManager.NETWORK_TYPE_GPRS -> false // ~ 100 kbps
                    TelephonyManager.NETWORK_TYPE_HSDPA -> true // ~ 2-14 Mbps
                    TelephonyManager.NETWORK_TYPE_HSPA -> true // ~ 700-1700 kbps
                    TelephonyManager.NETWORK_TYPE_HSUPA -> true // ~ 1-23 Mbps
                    TelephonyManager.NETWORK_TYPE_UMTS -> true // ~ 400-7000 kbps
                    TelephonyManager.NETWORK_TYPE_EHRPD -> true // ~ 1-2 Mbps
                    TelephonyManager.NETWORK_TYPE_EVDO_B -> true // ~ 5 Mbps
                    TelephonyManager.NETWORK_TYPE_HSPAP -> true // ~ 10-20 Mbps
                    TelephonyManager.NETWORK_TYPE_IDEN -> false // ~25 kbps
                    TelephonyManager.NETWORK_TYPE_LTE -> true // ~ 10+ Mbps
                    TelephonyManager.NETWORK_TYPE_UNKNOWN -> false
                    else -> false
                }
            } else {
                false
            }
        }
    }
}