package com.mx.dlna.util;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Message;

import java.net.InetAddress;

/**
 * 创建人： zhangmengxiong
 * 创建时间： 2017/9/6.
 * 联系方式: zmx_final@163.com
 */

public class NetUtils {
    public static void init(Context mContext) {
        WifiManager wifiManager = (WifiManager) mContext.getSystemService("wifi");
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();

        InetAddress inetAddress;
        Message message = new Message();
        try {
            inetAddress = InetAddress.getByName(String.format("%d.%d.%d.%d",
                    (ipAddress & 0xff), (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff),
                    (ipAddress >> 24 & 0xff)));

            hostName = inetAddress.getHostName();
            hostAddress = inetAddress.getHostAddress();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static InetAddress inetAddress;

    private static String hostAddress;

    private static String hostName;

    public static String getHostName() {
        return hostName;
    }

    public static String getHostAddress() {
        return hostAddress;
    }
}
