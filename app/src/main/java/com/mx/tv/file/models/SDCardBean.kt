package com.mx.tv.file.models

import java.io.File
import java.io.Serializable

/**
 * 创建人： zhangmengxiong
 * 创建时间： 2016-7-12.
 * 联系方式: zmx_final@163.com
 */
class SDCardBean : Serializable {

    constructor(file: File) {
        PATH = file.absolutePath
        if (file.exists()) {
            NAME = file.name
        }
    }

    constructor(path: String) : this(File(path))

    var PATH: String? = null
    var NAME: String? = null

    fun getFile(): File = File(PATH)

    fun getTotalSpace(): Long {
        val file = File(PATH)
        return if (file.exists()) file.totalSpace else 0L
    }

    fun getFreeSpace(): Long {
        val file = File(PATH)
        return if (file.exists()) file.freeSpace else 0L
    }

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is SDCardBean) return false

        return other.getTotalSpace() == getTotalSpace() && other.getFreeSpace() == getFreeSpace()
    }

    override fun toString(): String =
            NAME + ":[" + PATH + " , " + getFreeSpace() + "/" + getTotalSpace() + "]"
}
