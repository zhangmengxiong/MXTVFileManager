package com.mx.tv.file.samba

import com.mx.tv.file.utils.NetUtils
import jcifs.smb.SmbFile
import java.util.concurrent.Executors

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

    fun getFileList(server: SambaServer, path: String): ArrayList<SmbFile> {
        val list = ArrayList<SmbFile>()
        try {
            val smbFile = SmbFile(SambaUtil.fmtPath(server.IP, path, true))
            if (smbFile.isDirectory) {
                smbFile.listFiles().filter { !it.isHidden }.forEach {
                    list.add(it)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            list.clear()
        }
        return list
    }
}
