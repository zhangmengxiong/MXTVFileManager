package com.mx.tv.file.task


import android.os.AsyncTask
import com.mx.tv.file.models.FileTypeBean
import com.mx.tv.file.utils.FileScanUtil
import java.io.File

/**
 * 创建人： zhangmengxiong
 * 创建时间： 2016-7-14.
 * 联系方式: zmx_final@163.com
 */
class FileTypeScanTask(private val postExecute: AsyncPostExecute<List<File>>?, private val rootDir: String, private val fileTypeBean: FileTypeBean) : AsyncTask<String, String, List<File>>() {

    override fun onPreExecute() {
        postExecute?.onPreExecute()
    }

    override fun onProgressUpdate(vararg values: String) {
        postExecute?.onProgressUpdate(values[0])
    }

    override fun doInBackground(vararg strings: String): List<File> {
        return FileScanUtil.getChildFile(rootDir, fileTypeBean)
    }

    override fun onPostExecute(s: List<File>?) {
        postExecute?.onPostExecute(s != null, s)
    }
}
