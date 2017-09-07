package com.mx.tv.file.models

import android.text.TextUtils
import java.util.*

/**
 * 创建人： zhangmengxiong
 * 创建时间： 2016-7-12.
 * 联系方式: zmx_final@163.com
 */
enum class FileTypeBean {
    APK,
    PIC,
    VIDEO,
    SOUND,
    UNKNOW;


    companion object {
        fun fromName(name: String): FileTypeBean {
            var name = name
            if (TextUtils.isEmpty(name)) return UNKNOW
            name = name.toLowerCase(Locale.ENGLISH)
            if (name.endsWith(".apk")) return APK
            if (name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".gif"))
                return PIC
            if (name.endsWith(".mp4") || name.endsWith(".avi") || name.endsWith(".mov")) return VIDEO
            return if (name.endsWith(".mp3")) SOUND else UNKNOW
        }
    }
}
