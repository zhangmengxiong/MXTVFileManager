package com.mx.tv.file.utils

import java.text.DecimalFormat

/**
 * 创建人： zhangmengxiong
 * 创建时间： 2016-7-13.
 * 联系方式: zmx_final@163.com
 */
object StringFormate {
    /**
     * 文件大小转换为显示字符串
     *
     * @param size 单位:Byte
     * @return
     */
    fun fileSize2Show(size: Long): String {
        var result = 0f
        var danwei = ""
        if (size <= 0) {
            return "0 KB"
        }
        if (size < 1024 * 1024L) {
            result = size / 1024f
            danwei = " KB"
        } else if (size < 1024 * 1024 * 1024L) {
            result = size / (1024 * 1024f)
            danwei = " MB"
        } else if (size >= 1024 * 1024 * 1024L) {
            result = size / (1024f * 1024f * 1024f)
            danwei = " GB"
        }
        val reString = DecimalFormat("#.##").format(result.toDouble())
        return reString + danwei
    }
}
