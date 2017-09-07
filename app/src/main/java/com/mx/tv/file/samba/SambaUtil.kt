package com.mx.tv.file.samba

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

    fun fmtIP(ip: String): String = "$HEAD$ip"

    fun fmtPath(ip: String, path: String, isDir: Boolean = false): String = if (isDir) "$HEAD$ip$path/" else "$HEAD$ip$path"
}