package com.mx.lib.samba

import android.content.Context

/**
 * 创建人： zhangmengxiong
 * 创建时间： 2017/9/7.
 * 联系方式: zmx_final@163.com
 */
class SmbCache(context: Context) {


    companion object {
        private var INSTANCE: SmbCache? = null
        fun getInstance(context: Context): SmbCache {
            if (INSTANCE == null) {
                INSTANCE = SmbCache(context)
            }
            return INSTANCE!!
        }
    }
}