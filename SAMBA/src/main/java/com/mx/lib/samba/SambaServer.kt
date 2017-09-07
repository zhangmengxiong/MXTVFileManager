package com.mx.lib.samba

import java.io.Serializable

/**
 * 创建人： zhangmengxiong
 * 创建时间： 2017/9/1.
 * 联系方式: zmx_final@163.com
 */
data class SambaServer(
        var IP: String,
        var needLogin: Boolean,
        var serverName: String,
        var workSpace: String,
        var loginName: String = "",
        var passWord: String = "") : Serializable {

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is SambaServer) return false
        return IP == other.IP
    }

    override fun hashCode(): Int {
        var result = IP.hashCode()
        result = 31 * result + needLogin.hashCode()
        result = 31 * result + serverName.hashCode()
        result = 31 * result + workSpace.hashCode()
        result = 31 * result + loginName.hashCode()
        result = 31 * result + passWord.hashCode()
        return result
    }
}