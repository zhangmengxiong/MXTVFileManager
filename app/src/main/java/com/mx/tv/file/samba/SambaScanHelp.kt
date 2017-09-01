package com.mx.tv.file.samba

import com.mx.tv.file.utils.NetUtils
import java.util.concurrent.Executors
import kotlin.concurrent.thread

/**
 * 创建人： zhangmengxiong
 * 创建时间： 2017/8/31.
 * 联系方式: zmx_final@163.com
 */

object SambaScanHelp {
    private val SCAN_MAX = 255

    fun scan(scanCall: IScanCall?) {
        val ip = NetUtils.ip
        if (ip.isNullOrEmpty()) {
            scanCall?.onScanEnd()
            return
        }

        val executor = Executors.newFixedThreadPool(50)

        scanCall?.onStart()
        var finishSize = 0
        for (i in 0..SCAN_MAX) {
            executor.execute(ScanRun(ip, i,
                    {
                        finishSize++
                    },
                    { s ->
                        scanCall?.onFind(s)
                    }))
        }
        thread {
            while (finishSize < SCAN_MAX) {
                Thread.sleep(1000)
            }
            scanCall?.onScanEnd()
        }
    }

}
