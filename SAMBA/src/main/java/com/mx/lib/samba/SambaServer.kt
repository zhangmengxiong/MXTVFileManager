package com.mx.lib.samba

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

/**
 * 创建人： zhangmengxiong
 * 创建时间： 2017/9/1.
 * 联系方式: zmx_final@163.com
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class SambaServer(
        @JsonProperty("IP") var IP: String?,
        @JsonProperty("needLogin") var needLogin: Boolean,
        @JsonProperty("serverName") var serverName: String?,
        @JsonProperty("workSpace") var workSpace: String?,
        @JsonProperty("loginName") var loginName: String? = "",
        @JsonProperty("passWord") var passWord: String? = "") : Serializable {

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is SambaServer) return false
        return IP == other.IP
    }

    override fun hashCode(): Int {
        var result = IP?.hashCode() ?: 0
        result = 31 * result + needLogin.hashCode()
        result = 31 * result + (serverName?.hashCode() ?: 0)
        result = 31 * result + (workSpace?.hashCode() ?: 0)
        result = 31 * result + (loginName?.hashCode() ?: 0)
        result = 31 * result + (passWord?.hashCode() ?: 0)
        return result
    }
}