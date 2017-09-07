package com.mx.dlna;

import org.fourthline.cling.android.AndroidUpnpService;

/**
 * 创建人： zhangmengxiong
 * 创建时间： 2017/9/7.
 * 联系方式: zmx_final@163.com
 */

public interface IDlnaScan {
    void onStart(AndroidUpnpService upnpService);

    void findDevice(DeviceItem deviceItem);

    void removeDevice(DeviceItem deviceItem);

    void findMediaRenderer(DeviceItem deviceItem);

    void removeMediaRenderer(DeviceItem deviceItem);
}
