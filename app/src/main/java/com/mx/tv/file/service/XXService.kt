package com.mx.tv.file.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.mx.dlna.DeviceItem
import com.mx.dlna.DlnaScan
import com.mx.dlna.IDlnaScan
import jcifs.FileService
import org.fourthline.cling.android.AndroidUpnpService
import org.greenrobot.eventbus.EventBus
import com.mx.lib.samba.IScanCall
import com.mx.lib.samba.SambaScanHelp
import com.mx.lib.samba.SambaServer

class XXService : Service() {
    val devList = ArrayList<DeviceItem>()

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        startService(Intent(this, FileService::class.java))

        SambaScanHelp.scan(this, object : IScanCall {
            override fun onStart() {
                println("onStart()")
            }

            override fun onFind(sambaServer: SambaServer) {
                println("找到Samba服务器: ${sambaServer.IP}  是否需要登录：${sambaServer.needLogin} " +
                        "服务器名字：${sambaServer.serverName}")

                EventBus.getDefault().post(sambaServer)
            }

            override fun onScanEnd() {
                println("onScanEnd()")
            }
        })

        DlnaScan(this).scan(object : IDlnaScan {
            override fun onStart(upnpService: AndroidUpnpService?) {

            }

            override fun findDevice(deviceItem: DeviceItem?) {
                deviceItem?.let {
                    if (!devList.contains(it)) {
                        println("findDevice()" + it.device.toString())
                        devList.add(it)
                        EventBus.getDefault().post(it)
                    }
                }
            }

            override fun removeDevice(deviceItem: DeviceItem?) {
                deviceItem?.let {
                    if (devList.contains(it)) {
                        println("removeDevice()" + it.device.toString())
                        devList.remove(it)
                    }
                }
            }

            override fun findMediaRenderer(deviceItem: DeviceItem?) {
                println("findMediaRenderer()" + deviceItem?.device.toString())
            }

            override fun removeMediaRenderer(deviceItem: DeviceItem?) {
                println("removeMediaRenderer()" + deviceItem?.device.toString())
            }
        })
    }
}
