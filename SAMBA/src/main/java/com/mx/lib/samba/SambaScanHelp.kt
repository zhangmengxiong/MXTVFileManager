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
        val executor = Executors.newFixedThreadPool(30)
        scanCall?.onStart()

        val ipList = ArrayList<String>()
        SmbCache.getInstance(context).getSmbList().forEach {
            ipList.add(it.IP!!)
        }
        for (i in 0..SCAN_MAX) {
            val cip = SambaUtil.getIP(ip, i)
            if (!ipList.contains(cip)) {
                ipList.add(cip)
            }
        }

        var finishSize = 0
        val maxSize = ipList.size

        ipList.forEach {
            println(it)
            executor.execute(ServerScanRun(it,
                    {
                        finishSize++
                        if (finishSize >= maxSize) {
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
        smbFile.connect()
        if (smbFile.isDirectory) {
            return smbFile.listFiles().filter { !it.isHidden }
        }
        return emptyList()
    }
}
