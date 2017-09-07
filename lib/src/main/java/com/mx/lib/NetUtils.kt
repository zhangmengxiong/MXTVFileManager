package com.mx.lib

import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.util.Log
import java.net.*
import java.util.*

/**
 * 创建人： zhangmengxiong
 * 创建时间： 2017/8/31.
 * 联系方式: zmx_final@163.com
 */

object NetUtils {
    private val TAG = NetUtils::class.java.name


    fun getIp(context: Context): String {
        val address = getLocalInetAddress(context.applicationContext)
        if (address != null) {
            return address.hostAddress
        }
        try {
            val enNI: Enumeration<NetworkInterface> = NetworkInterface.getNetworkInterfaces()
            while (enNI
                    .hasMoreElements()) {
                val enumIpAddr = enNI.nextElement()
                        .inetAddresses
                while (enumIpAddr.hasMoreElements()) {
                    val localInetAddress = enumIpAddr.nextElement()
                    if (!localInetAddress.isLoopbackAddress && !localInetAddress.isLinkLocalAddress)
                        return localInetAddress.hostAddress.toString()
                }
            }
        } catch (localSocketException: SocketException) {
            Log.e("PhoneActivity:", "can not get LocalIpAddress!")
        }

        return ""
    }

    /**
     * Gets the local ip address
     *
     * @return local ip adress or null if not found
     */
    private fun getLocalInetAddress(context: Context?): InetAddress? {
        if (!isNetConnected(context)) {
            Log.e(TAG, "getLocalInetAddress called and no connection")
            return null
        }
        // TODO: next if block could probably be removed
        if (isWIFIConnected(context)) {
            val wm = context!!.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val ipAddress = wm.connectionInfo.ipAddress
            return if (ipAddress == 0) null else intToInet(ipAddress)
        }
        // This next part should be able to get the local ip address, but ii
        // some case
        // I'm receiving the routable address
        try {
            val netinterfaces = NetworkInterface.getNetworkInterfaces()
            while (netinterfaces.hasMoreElements()) {
                val netinterface = netinterfaces.nextElement()
                val adresses = netinterface
                        .inetAddresses
                while (adresses.hasMoreElements()) {
                    val address = adresses.nextElement()
                    // this is the condition that sometimes gives problems
                    if (!address.isLoopbackAddress && !address.isLinkLocalAddress)
                        return address
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    /**
     * Checks to see if we are connected using wifi
     *
     * @param context
     * @return true if connected using wifi
     */
    private fun isWIFIConnected(context: Context?): Boolean {
        if (context != null) {
            val mConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val mWiFiNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
            if (mWiFiNetworkInfo != null) {
                return mWiFiNetworkInfo.isAvailable
            }
        }
        return false
    }

    private fun intToInet(value: Int): InetAddress? {
        val bytes = ByteArray(4)
        for (i in 0..3) {
            bytes[i] = byteOfInt(value, i)
        }
        try {
            return InetAddress.getByAddress(bytes)
        } catch (e: UnknownHostException) {
            // This only happens if the byte array has a bad length
            return null
        }

    }

    private fun byteOfInt(value: Int, which: Int): Byte {
        val shift = which * 8
        return (value shr shift).toByte()
    }

    /**
     * Checks to see if we are connected to a local network, for instance wifi
     * or ethernet
     *
     * @param context
     * @return true if connected to a local network
     */
    private fun isNetConnected(context: Context?): Boolean {
        var connected = false
        val cm = context!!
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val ni = cm.activeNetworkInfo
        connected = ni != null && ni.isConnected
                && ni.type and (ConnectivityManager.TYPE_WIFI or ConnectivityManager.TYPE_ETHERNET) != 0
        if (!connected) {
            Log.d(TAG, "Device not connected to a network, see if it is an AP")
            val wm = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            try {
                val method = wm.javaClass.getDeclaredMethod(
                        "isWifiApEnabled")
                connected = method.invoke(wm) as Boolean
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
        return connected
    }

    fun isHostConnectable(host: String, port: Int): Boolean {
        val socket = Socket()
        try {
            socket.soTimeout = 1000 * 5
            socket.connect(InetSocketAddress(host, port))
        } catch (e: Exception) {
            return false
        } finally {
            try {
                socket.close()
            } catch (ignored: Exception) {
            }
        }
        return true
    }
}