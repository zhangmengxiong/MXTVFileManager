package com.mx.lib.samba

import android.content.Context
import com.mx.lib.NetUtils
import jcifs.smb.SmbFile
import java.util.concurrent.Executors

/**
 * 创建人： zhangmengxiong
 * 创建时间： 2017/8/31.
 * 联系方式: zmx_final@163.com
 */

object SambaScanHelp {
    private val SCAN_MAX = 255

    fun scan(context: Context, scanCall: IScanCall?) {
        val ip = NetUtils.getIp(context)
        if (ip.isEmpty()) {
            scanCall?.onScanEnd()
            return
        }

        val executor = Executors.newFixedThreadPool(20)

        scanCall?.onStart()
        var finishSize = 0
        for (i in 0..SCAN_MAX) {
            executor.execute(ServerScanRun(ip, i,
                    {
                        finishSize++
                        if (finishSize >= SCAN_MAX) {
                            scanCall?.onScanEnd()
                        }
                    },
                    { s ->
                        scanCall?.onFind(s)
                    }))
        }
    }

    fun getFileList(server: SambaServer, path: String): List<SmbFile> {
        val smbFile = SmbFile(SambaUtil.fmtPath(server, path, true))
        if (smbFile.isDirectory) {
            return smbFile.listFiles().filter { !it.isHidden }
        }
        return emptyList()
    }
}
