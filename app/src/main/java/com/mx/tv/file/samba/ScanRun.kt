package com.mx.tv.file.samba

import com.mx.tv.file.utils.NetUtils
import jcifs.smb.SmbAuthException
import jcifs.smb.SmbFile


/**
 * 创建人： zhangmengxiong
 * 创建时间： 2017/8/31.
 * 联系方式: zmx_final@163.com
 */
class ScanRun(private val ip: String, private val index: Int, private val endCall: () -> Unit, private val findCall: ((SambaServer) -> Unit)) : Runnable {
    override fun run() {
        val ip = SambaUtil.getIP(ip, index)
        try {
            var isFind = false
            if (NetUtils.isHostConnectable(ip, SambaUtil.POET1)) {
                println("找到可疑端口：$ip:${SambaUtil.POET1}")
                isFind = true
            } else if (NetUtils.isHostConnectable(ip, SambaUtil.POET2)) {
                println("找到可疑端口：$ip:${SambaUtil.POET2}")
                isFind = true
            }
            if (isFind) {
                try {
                    val smbFile = SmbFile(SambaUtil.fmtIP(ip))
                    if (smbFile.isDirectory) {
                        smbFile.listFiles().filter { !it.isHidden }.forEach {
                            println(it.path)
                        }
                        findCall(SambaServer(ip, false))
                    }
                } catch (e: SmbAuthException) {
                    findCall(SambaServer(ip, true))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            endCall()
        }
    }
}