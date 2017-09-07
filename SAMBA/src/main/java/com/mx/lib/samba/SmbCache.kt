package com.mx.lib.samba

import android.content.Context
import android.content.SharedPreferences
import com.fasterxml.jackson.databind.ObjectMapper
import com.mx.lib.toJson

/**
 * 创建人： zhangmengxiong
 * 创建时间： 2017/9/7.
 * 联系方式: zmx_final@163.com
 */
class SmbCache(context: Context) {
    private val sp: SharedPreferences = context.applicationContext.getSharedPreferences("smb_cache", Context.MODE_PRIVATE)

    private val TAG = "SmbCache"
    private val smbList: ArrayList<SambaServer> = ArrayList()

    init {
        val mapper = ObjectMapper()
        try {
            val json = sp.getString(TAG, "")
            val jsonNode = mapper.readTree(json)
            println(json)
            jsonNode?.forEach {
                smbList.add(mapper.treeToValue(it, SambaServer::class.java))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun saveCache(smbServer: SambaServer) {
        if (!smbList.contains(smbServer)) {
            smbList.add(smbServer)
            save()
        }
    }

    fun updateCache(smbServer: SambaServer) {
        smbList.removeAll {
            it == smbServer
        }
        if (!smbList.contains(smbServer)) {
            smbList.add(smbServer)
            save()
        }
    }

    fun fillCache(smbServer: SambaServer): SambaServer {
        smbList.firstOrNull {
            it == smbServer
        }?.apply {
            smbServer.loginName = loginName
            smbServer.passWord = passWord
        }
        return smbServer
    }

    fun getSmbList(): ArrayList<SambaServer> = smbList

    private fun save() {
        sp.edit().putString(TAG, smbList.toJson()).commit()
    }

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