package com.mx.lib.samba

/**
 * 创建人： zhangmengxiong
 * 创建时间： 2017/9/1.
 * 联系方式: zmx_final@163.com
 */
interface IScanCall {
    fun onStart()

    fun onFind(sambaServer: SambaServer)

    fun onScanEnd()
}