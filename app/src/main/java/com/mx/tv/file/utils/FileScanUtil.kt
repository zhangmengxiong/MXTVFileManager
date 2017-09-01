package com.mx.tv.file.utils

import android.util.Log
import com.mx.tv.file.models.FileBean
import com.mx.tv.file.models.FileTypeBean
import com.mx.tv.file.models.SDCardBean
import java.io.File
import java.util.*

/**
 * 创建人： zhangmengxiong
 * 创建时间： 2016-7-12.
 * 联系方式: zmx_final@163.com
 */
object FileScanUtil {
    private val TAG = FileScanUtil::class.java.simpleName

    /**
     * 过滤视频的参数组，大小写不敏感
     */
    private val VIDEO_TAIL = arrayOf("*.[Mm][Pp]4", "*.[Ww][Mm][Vv]", "*.[Mm][Oo][Vv]", "*.[Aa][Vv][Ii]", "*.[Mm][Pp][Ee][Gg]", "*.[Mm][Pp][Gg]")//avi、mpg、mov、swf mp4 mpeg wmv
    /**
     * 图片的过滤参数组，大小写不敏感
     */
    private val PIC_TAIL = arrayOf("*.[Bb][Mm][Pp]", "*.[Gg][Ii][Ff]", "*.[Pp][Nn][Gg]", "*.[Pp][Ii][Cc]", "*.[Tt][Ii][Ff]", "*.[Jj][Pp][Gg]")//bmp、gif、jpg、pic、png、tif
    /**
     * 过滤应用的参数组，大小写不敏感
     */
    private val APK_TAIL = arrayOf("*.[Aa][Pp][Kk]")

    /**
     * 过滤声音文件的参数组，过滤大小写
     */
    private val SOUND_TAIL = arrayOf("*.[Mm][Pp]3", "*.[Ww][Mm][Aa]", "*.[Aa][Aa][Cc]", "*.[Ww][Aa][Vv]")//wav 、aif 、au 、mp3 、ram 、wma、mmf、amr、aac、flac


    /**
     * 文件夹排序器
     */
    private val FILE_SORT = Comparator<File> { t, t1 ->
        if (t != null && t1 != null) {
            val isTDir = t.isDirectory
            val isT1Dir = t1.isDirectory

            if (isTDir && isT1Dir) {
                return@Comparator t.name.compareTo(t1.name)
            }
            if (isTDir) return@Comparator -1

            return@Comparator if (isT1Dir) 1 else t.name.compareTo(t1.name)

        }

        if (t != null) return@Comparator -1
        if (t1 != null) 0 else 1
    }

    /**
     * 获取目录下的子目录列表
     *
     * @param path 路径
     * @return 排序后的目录列表
     */
    fun getFileList(path: String): ArrayList<FileBean> {
        val result = ArrayList<FileBean>()
        try {
            val file = File(path)
            if (file.exists() && file.isDirectory) {
                val files = file.listFiles()
                if (files != null && files.isNotEmpty()) {
                    Arrays.sort(files, FILE_SORT)

                    files.mapTo(result) { FileBean(it) }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return result
    }

    /**
     * 获取目录中子文件的个数，包含子目录中的文件个数
     *
     * @param path 目录路径
     * @return 返回文件个数
     */
    fun getChildFileSize(path: String): Long {
        try {
            val cmds = arrayOf("cd " + path, "find ./ -type f -print | wc -l")
            var result = CMDUtils.execCMD(cmds, false)
            if (result.contains("\r\n")) {
                val s = result.split("\r\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                result = s[s.size - 1]
            }
            return java.lang.Long.parseLong(result)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return 0L
    }

    /**
     * 获取目录中子文件的个数，包含子目录中的文件个数
     *
     * @param path 目录路径
     * @return 返回文件个数
     */
    fun getChildFileSize(path: String, typeBean: FileTypeBean): Long {
        // 实现方式：用linux命令 find 来查找文件目录下的对应后缀的数量！包含子文件夹！
        //find ./ \( -name '*.png' -o -name '*.apk' \) -print | wc -l
        var size = 0L
        try {
            var spill: Array<String>? = null
            /**
             * 获取过滤后缀
             */
            when (typeBean) {
                FileTypeBean.VIDEO -> spill = VIDEO_TAIL
                FileTypeBean.PIC -> spill = PIC_TAIL
                FileTypeBean.APK -> spill = APK_TAIL
                FileTypeBean.SOUND -> spill = SOUND_TAIL
                else -> {
                }
            }
            spill!!
            if (spill.isNotEmpty()) {
                // find命令拼接！
                var cmd = "find ./ \\("
                for (i in spill.indices) {
                    if (i > 0) {
                        cmd = cmd + " -o -name '" + spill[i] + "'"
                    } else {
                        cmd = cmd + " -name '" + spill[i] + "'"
                    }
                }
                cmd = "$cmd \\) -print | wc -l"
                //            Log.v(TAG, "cmd = " + cmd);

                // 执行命令
                var result = CMDUtils.execCMD(arrayOf("cd " + path, cmd), false)
                if (result.contains("\r\n")) {
                    val ss = result.split("\r\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    result = ss[ss.size - 1]
                }
                size = java.lang.Long.parseLong(result)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return size
    }

    /**
     * 获取目录中子文件的个数，包含子目录中的文件个数
     *
     * @param path 目录路径
     * @return 返回文件个数
     */
    fun getChildFile(path: String, typeBean: FileTypeBean, findCall: FindCall?) {
        // 实现方式：用linux命令 find 来查找文件目录下的对应后缀的列表！包含子文件夹！
        //find ./ \( -name '*.png' -o -name '*.apk' \)
        try {
            var spill: Array<String>? = null
            when (typeBean) {
                FileTypeBean.VIDEO -> spill = VIDEO_TAIL
                FileTypeBean.PIC -> spill = PIC_TAIL
                FileTypeBean.APK -> spill = APK_TAIL
                FileTypeBean.SOUND -> spill = SOUND_TAIL
                else -> {
                }
            }
            spill!!
            if (spill.isNotEmpty()) {
                // find命令拼接！
                var cmd = "find ./ \\("
                for (i in spill.indices) {
                    if (i > 0) {
                        cmd = cmd + " -o -name '" + spill[i] + "'"
                    } else {
                        cmd = cmd + " -name '" + spill[i] + "'"
                    }
                }
                cmd = "$cmd \\) "
                CMDUtils.execCMD(arrayOf("cd " + path, cmd), false, object : CMDUtils.ResultCall {
                    override fun newLine(s: String) {
                        try {
                            val s1 = path + s.substring(1)
                            val f = File(s1)
                            if (f.exists() && findCall != null) {
                                findCall.findOneFile(f.absolutePath)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                })
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * 获取本地磁盘所有的挂载U盘
     *
     * @return
     */
    val sdList: ArrayList<SDCardBean>
        get() {
            val result = ArrayList<SDCardBean>()
            try {
                val file = File("/sdcard")
                if (file.exists() && file.canRead() && file.canWrite()) {
                    val bean = SDCardBean(file)
                    bean.NAME = "本地磁盘"
                    result.add(bean)
                }

                val cmdResult = CMDUtils.execCMD("mount", false)
                if (cmdResult != null && cmdResult.length > 0) {
                    val keys = cmdResult.split("\r\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    for (key in keys) {
                        val ss = key.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                        if (ss.size >= 2) {
                            val path = File(ss[1])
                            if (path.exists() && path.canWrite() && path.canRead() && path.parentFile.absolutePath != "/") {
                                val bean = SDCardBean(path)
                                var isnew = true
                                for (cardBean in result) {
                                    if (cardBean == bean) {
                                        isnew = false
                                    }
                                }
                                if (isnew) {
                                    result.add(bean)
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            Log.v(TAG, "找到新挂载磁盘：" + Arrays.toString(result.toTypedArray()))
            return result
        }

    /**
     * 文件查找器回掉接口
     * 避免一次查找到太多文件时，处理数据溢出
     */
    interface FindCall {
        /**
         * 找到一条记录
         *
         * @param path 路径
         */
        fun findOneFile(path: String)
    }
}
