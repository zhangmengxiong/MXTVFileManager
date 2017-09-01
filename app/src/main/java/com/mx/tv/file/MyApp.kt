package com.mx.tv.file

import android.app.Application
import android.content.Context
import android.content.Intent

import com.mx.tv.file.service.XXService

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
        startService(Intent(this, XXService::class.java))
    }

    companion object {
        val executor: Executor = Executors.newCachedThreadPool()
        var appContext: Context? = null
            private set
    }
}
