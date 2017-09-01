package com.mx.tv.file.samba

/**
 * 创建人： zhangmengxiong
 * 创建时间： 2017/9/1.
 * 联系方式: zmx_final@163.com
 */
data class SambaServer(val IP: String, val needLogin: Boolean, val name: String = "", val pass: String = "")