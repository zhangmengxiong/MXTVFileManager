package com.mx.tv.file.base

import android.app.Application
import android.content.Context
import android.content.Intent
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper

import com.mx.tv.file.service.XXService
import com.mx.tv.file.utils.CacheBiz

import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * 创建人： zhangmengxiong
 * 创建时间： 2016-7-28.
 * 联系方式: zmx_final@163.com
 */
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        appContext = this
        CacheBiz.init(this)
        val dm = this.resources.displayMetrics
        _screenWidth = dm.widthPixels
        _screenHeight = dm.heightPixels


        startService(Intent(this, XXService::class.java))
    }

    companion object {
        val executor: Executor = Executors.newCachedThreadPool()
        var appContext: Context? = null
            private set

        private var _screenWidth: Int = 0

        private var _screenHeight: Int = 0
            private set

        fun getScreenWidth(): Int = _screenWidth

        fun getScreenHeight(): Int = _screenHeight
    }
}

fun Any.toJson(): String {
    val mapper = ObjectMapper()
    try {
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
        return mapper.writeValueAsString(this)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return ""
}

