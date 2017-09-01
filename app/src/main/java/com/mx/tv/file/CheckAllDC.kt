package com.mx.tv.file

import jcifs.UniAddress
import jcifs.netbios.NbtAddress
import jcifs.smb.NtlmPasswordAuthentication
import jcifs.smb.SmbSession

/**
 * 创建人： zhangmengxiong
 * 创建时间： 2017/9/1.
 * 联系方式: zmx_final@163.com
 */

object CheckAllDC {

    @Throws(Exception::class)
    @JvmStatic
    fun main(argv: Array<String>) {

        if (argv.size < 2) {
            System.err.println("usage: CheckAllDC <domain> <dom;user:pass>")
            System.exit(1)
        }

        val addrs = NbtAddress.getAllByName(argv[0], 0x1C, null, null)

        for (i in addrs.indices) {
            println(addrs[i])
            val dc = UniAddress(addrs[i])
            val auth = NtlmPasswordAuthentication(argv[1])
            SmbSession.logon(dc, auth)
        }
    }
}
