package com.mx.tv.file.task

import android.os.AsyncTask
import com.mx.tv.file.utils.FileScanUtil
import java.io.File
import java.util.*

/**
 * 创建人： zhangmengxiong
 * 创建时间： 2016-7-14.
 * 联系方式: zmx_final@163.com
 */
class FileScanTask(private val postExecute: AsyncPostExecute<ArrayList<File>>?) : AsyncTask<String, String, ArrayList<File>>() {

    override fun onPreExecute() {
        postExecute?.onPreExecute()
    }

    override fun onProgressUpdate(vararg values: String) {
        postExecute?.onProgressUpdate(values[0])
    }

    override fun doInBackground(vararg strings: String): ArrayList<File> {
        return FileScanUtil.getFileList(strings[0])
    }

    override fun onPostExecute(s: ArrayList<File>?) {
        postExecute?.onPostExecute(s != null, s)
    }
}
