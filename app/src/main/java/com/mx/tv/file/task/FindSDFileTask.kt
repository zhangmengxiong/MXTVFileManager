package com.mx.tv.file.task

import android.os.AsyncTask

import com.mx.tv.file.models.FileTypeBean
import com.mx.tv.file.models.SDScanItem
import com.mx.tv.file.utils.FileScanUtil

/**
 * 创建人： zhangmengxiong
 * 创建时间： 2016-7-14.
 * 联系方式: zmx_final@163.com
 */
class FindSDFileTask(private val postExecute: AsyncPostExecute<String>?) : AsyncTask<String, SDScanItem, String>() {

    override fun onPreExecute() {
        postExecute?.onPreExecute()
    }

    override fun onProgressUpdate(vararg values: SDScanItem) {
        postExecute?.onProgressUpdate(values[0])
    }

    override fun doInBackground(vararg strings: String): String? {
        val rootPath = strings[0]

        publishProgress(SDScanItem(FileTypeBean.UNKNOW, FileScanUtil.getChildFileSize(rootPath)))
        publishProgress(SDScanItem(FileTypeBean.APK, FileScanUtil.getChildFileSize(rootPath, FileTypeBean.APK)))
        publishProgress(SDScanItem(FileTypeBean.PIC, FileScanUtil.getChildFileSize(rootPath, FileTypeBean.PIC)))
        publishProgress(SDScanItem(FileTypeBean.SOUND, FileScanUtil.getChildFileSize(rootPath, FileTypeBean.SOUND)))
        publishProgress(SDScanItem(FileTypeBean.VIDEO, FileScanUtil.getChildFileSize(rootPath, FileTypeBean.VIDEO)))
        return null
    }

    override fun onPostExecute(s: String?) {
        postExecute?.onPostExecute(s != null, s)
    }

    companion object {
        private val TAG = FindSDFileTask::class.java.simpleName
    }
}
