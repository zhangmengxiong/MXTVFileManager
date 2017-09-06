package com.mx.tv.file.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.mx.tv.file.samba.IScanCall
import com.mx.tv.file.samba.SambaScanHelp
import com.mx.tv.file.samba.SambaServer

class XXService : Service() {

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        Thread {
            SambaScanHelp.scan(object : IScanCall {
                override fun onStart() {
                    println("onStart()")
                }

                override fun onFind(sambaServer: SambaServer) {
                    println("找到Samba服务器: ${sambaServer.IP}  是否需要登录：${sambaServer.needLogin} " +
                            "服务器名字：${sambaServer.serverName}")
                }

                override fun onScanEnd() {
                    println("onScanEnd()")
                }
            })
        }.start()
    }
}
