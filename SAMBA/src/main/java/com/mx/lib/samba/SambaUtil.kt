package com.mx.lib.samba

/**
 * 创建人： zhangmengxiong
 * 创建时间： 2017/8/31.
 * 联系方式: zmx_final@163.com
 */
object SambaUtil {
    private val HEAD: String = "smb://"
    val POET1 = 445
    val POET2 = 139

    fun getIP(ip: String, index: Int): String {
        val i = ip.lastIndexOf(".")
        return ip.substring(IntRange(0, i)).plus(index)
    }

    fun fmtIP(ip: String): String = "$HEAD$ip/"

    fun fmtPath(server: SambaServer, path: String, isDir: Boolean = false): String {
        return if (server.needLogin) {
            if (isDir) "$HEAD${server.loginName}:${server.passWord}@${server.IP}$path/" else "$HEAD${server.IP}$path"
        } else {
            if (isDir) "$HEAD${server.IP}$path/" else "$HEAD${server.IP}$path"
        }
    }
}