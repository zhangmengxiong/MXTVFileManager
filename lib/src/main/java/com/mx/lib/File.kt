package com.mx.lib

import com.fasterxml.jackson.databind.ObjectMapper

/**
 * 创建人： zhangmengxiong
 * 创建时间： 2017/9/7.
 * 联系方式: zmx_final@163.com
 */
fun Any?.toJson(): String {
    if (this == null) return ""
    val mapper = ObjectMapper()
    try {
        return mapper.writeValueAsString(this)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return ""
}