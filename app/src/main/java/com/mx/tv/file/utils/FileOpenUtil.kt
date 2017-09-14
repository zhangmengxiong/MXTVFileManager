package com.mx.tv.file.utils

import android.content.Context
import android.content.Intent
import android.net.Uri

import java.io.File
import java.util.*

/**
 * 创建人： zhangmengxiong
 * 创建时间： 2016-7-15.
 * 联系方式: zmx_final@163.com
 */
object FileOpenUtil {
    private val TAG = FileOpenUtil::class.java.simpleName

    /**
     * 打开文件
     *
     * @param file
     */
    fun openFile(context: Context, file: File): Boolean = openFile(context, file.absolutePath)

    /**
     * 打开文件
     *
     * @param file
     */
    fun openFile(context: Context, url: String): Boolean {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        var isOK: Boolean = try {
            //获取文件file的MIME类型
            val type = getMIMEType(url)
            //设置intent的data和Type属性。
            intent.setDataAndType(Uri.parse(url), type)
            //跳转
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            false
        }

        if (!isOK) {
            intent.setDataAndType(Uri.parse(url), "*/*")
            try {
                //跳转
                context.startActivity(intent)
                isOK = true
            } catch (e: Exception) {
                isOK = false
                e.printStackTrace()
            }

        }
        return isOK
    }

    /**
     * 根据文件后缀名获得对应的MIME类型。
     *
     * @param file
     */
    private fun getMIMEType(file: String): String {
        val file = file.toLowerCase(Locale.ENGLISH)
        //在MIME和文件类型的匹配表中找到对应的MIME类型。
        val table = MIME_MapTable.firstOrNull { file.endsWith(it[0]) }
        return if (table == null) "*/*" else table[1]
    }

    private val MIME_MapTable = arrayOf(
            //{后缀名，MIME类型}
            arrayOf(".3gp", "video/3gpp"),
            arrayOf(".apk", "application/vnd.android.package-archive"),
            arrayOf(".asf", "video/x-ms-asf"),
            arrayOf(".avi", "video/x-msvideo"),
            arrayOf(".bin", "application/octet-stream"),
            arrayOf(".bmp", "image/bmp"),
            arrayOf(".c", "text/plain"),
            arrayOf(".class", "application/octet-stream"),
            arrayOf(".conf", "text/plain"),
            arrayOf(".cpp", "text/plain"),
            arrayOf(".doc", "application/msword"),
            arrayOf(".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
            arrayOf(".xls", "application/vnd.ms-excel"),
            arrayOf(".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
            arrayOf(".exe", "application/octet-stream"),
            arrayOf(".gif", "image/gif"),
            arrayOf(".gtar", "application/x-gtar"),
            arrayOf(".gz", "application/x-gzip"),
            arrayOf(".h", "text/plain"),
            arrayOf(".htm", "text/html"),
            arrayOf(".html", "text/html"),
            arrayOf(".jar", "application/java-archive"),
            arrayOf(".java", "text/plain"),
            arrayOf(".jpeg", "image/jpeg"),
            arrayOf(".jpg", "image/jpeg"),
            arrayOf(".js", "application/x-javascript"),
            arrayOf(".log", "text/plain"),
            arrayOf(".m3u", "audio/x-mpegurl"),
            arrayOf(".m4a", "audio/mp4a-latm"),
            arrayOf(".m4b", "audio/mp4a-latm"),
            arrayOf(".m4p", "audio/mp4a-latm"),
            arrayOf(".m4u", "video/vnd.mpegurl"),
            arrayOf(".m4v", "video/x-m4v"),
            arrayOf(".mov", "video/quicktime"),
            arrayOf(".mp2", "audio/x-mpeg"),
            arrayOf(".mp3", "audio/x-mpeg"),
            arrayOf(".mp4", "video/mp4"),
            arrayOf(".mpc", "application/vnd.mpohun.certificate"),
            arrayOf(".mpe", "video/mpeg"),
            arrayOf(".mpeg", "video/mpeg"),
            arrayOf(".mpg", "video/mpeg"),
            arrayOf(".mpg4", "video/mp4"),
            arrayOf(".mpga", "audio/mpeg"),
            arrayOf(".msg", "application/vnd.ms-outlook"),
            arrayOf(".ogg", "audio/ogg"),
            arrayOf(".pdf", "application/pdf"),
            arrayOf(".png", "image/png"),
            arrayOf(".pps", "application/vnd.ms-powerpoint"),
            arrayOf(".ppt", "application/vnd.ms-powerpoint"),
            arrayOf(".pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation"),
            arrayOf(".prop", "text/plain"),
            arrayOf(".rc", "text/plain"),
            arrayOf(".rmvb", "audio/x-pn-realaudio"),
            arrayOf(".rtf", "application/rtf"),
            arrayOf(".sh", "text/plain"),
            arrayOf(".tar", "application/x-tar"),
            arrayOf(".tgz", "application/x-compressed"),
            arrayOf(".txt", "text/plain"),
            arrayOf(".wav", "audio/x-wav"),
            arrayOf(".wma", "audio/x-ms-wma"),
            arrayOf(".wmv", "audio/x-ms-wmv"),
            arrayOf(".wps", "application/vnd.ms-works"),
            arrayOf(".xml", "text/plain"),
            arrayOf(".z", "application/x-compress"),
            arrayOf(".zip", "application/x-zip-compressed"),
            arrayOf("", "*/*"))
}
