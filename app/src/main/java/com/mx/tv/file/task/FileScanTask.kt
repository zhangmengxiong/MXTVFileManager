package com.mx.tv.file.task

import android.os.AsyncTask

import com.mx.tv.file.models.FileBean
import com.mx.tv.file.utils.FileScanUtil

import java.util.ArrayList

/**
 * 创建人： zhangmengxiong
 * 创建时间： 2016-7-14.
 * 联系方式: zmx_final@163.com
 */
class FileScanTask(private val postExecute: AsyncPostExecute<ArrayList<FileBean>>?) : AsyncTask<String, String, ArrayList<FileBean>>() {

    override fun onPreExecute() {
        postExecute?.onPreExecute()
    }

    override fun onProgressUpdate(vararg values: String) {
        postExecute?.onProgressUpdate(values[0])
    }

    override fun doInBackground(vararg strings: String): ArrayList<FileBean> {
        return FileScanUtil.getFileList(strings[0])
    }

    override fun onPostExecute(s: ArrayList<FileBean>?) {
        postExecute?.onPostExecute(s != null, s)
    }

    companion object {

        private val TAG = FindSDFileTask::class.java.simpleName
    }
}
