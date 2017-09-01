package com.mx.tv.file.models

import java.io.File
import java.io.Serializable

/**
 * 创建人： zhangmengxiong
 * 创建时间： 2016-7-12.
 * 联系方式: zmx_final@163.com
 */
class SDCardBean(var FILE: File?) : Serializable {
    var NAME: String? = null

    init {
        if (FILE != null && FILE!!.exists()) {
            NAME = FILE!!.name
        }
    }

    val totalSpace: Long
        get() = if (FILE != null) FILE!!.totalSpace else 0L

    val freeSpace: Long
        get() = if (FILE != null) FILE!!.freeSpace else 0L

    override fun equals(o: Any?): Boolean {
        if (o == null) return false
        val bean = o as SDCardBean?

        return bean!!.totalSpace == totalSpace && bean.freeSpace == freeSpace
    }

    override fun toString(): String {
        return NAME + ":[" + FILE!!.absolutePath + "," + freeSpace + "/" + totalSpace + "]"
    }
}
