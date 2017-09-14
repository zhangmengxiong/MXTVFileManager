package com.mx.tv.file.models


import java.io.File
import java.io.Serializable

/**
 * 创建人： zhangmengxiong
 * 创建时间： 2016-7-12.
 * 联系方式: zmx_final@163.com
 */
class FileBean(var FILE: File) : Serializable {
    var NAME: String? = null
    var TYPE: FileTypeBean? = null
    var CHILD_SIZE: Int = 0

    init {
        if (FILE.exists()) {
            NAME = FILE.name
            TYPE = FileTypeBean.fromName(NAME!!)
            if (FILE.isDirectory) {
                val files = FILE.listFiles()
                CHILD_SIZE = files?.size ?: 0
            }
        }
    }

    fun exists(): Boolean {
        return FILE.exists()
    }

    val isDirectory: Boolean
        get() = FILE.isDirectory

    override fun toString(): String {
        return NAME + "[" + (if (isDirectory) "dir" else "file") + "]"
    }
}
