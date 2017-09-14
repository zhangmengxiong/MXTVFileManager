package com.mx.tv.file.utils

import android.content.Context
import android.content.SharedPreferences
import com.fasterxml.jackson.databind.ObjectMapper
import com.mx.tv.file.base.toJson
import com.mx.tv.file.models.SDCardBean

/**
 * 创建人： zhangmengxiong
 * 创建时间： 2017/9/14.
 * 联系方式: zmx_final@163.com
 */
object CacheBiz {
    private val CACHE_NAME = CacheBiz::class.java.simpleName
    private val SD_KEY = "SD_KEY"

    private var sharePP: SharedPreferences? = null

    private val sdList = ArrayList<SDCardBean>()
    fun init(context: Context) {
        sharePP = context.getSharedPreferences(CACHE_NAME, Context.MODE_PRIVATE)
        sdList.clear()

        val json = sharePP?.getString(SD_KEY, "") ?: ""
        if (!json.isBlank()) {
            val mapper = ObjectMapper()
            try {
                val jsonNode = mapper.readTree(json)
                jsonNode?.forEach {
                    sdList.add(mapper.treeToValue(it, SDCardBean::class.java))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun addSDCardItem(card: SDCardBean) {
        if (!sdList.contains(card)) {
            sdList.add(card)
            save(SD_KEY, sdList)
        }
    }

    private fun save(key: String, obj: Any) {
        sharePP?.edit()?.putString(key, obj.toJson())?.commit()
    }

    fun getSDCacheList() = sdList
}