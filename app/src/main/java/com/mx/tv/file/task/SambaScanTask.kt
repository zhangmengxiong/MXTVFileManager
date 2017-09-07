package com.mx.tv.file.task

import android.os.AsyncTask
import com.mx.tv.file.samba.SambaScanHelp
import com.mx.tv.file.samba.SambaServer
import jcifs.smb.SmbFile
import java.util.*

/**
 * 创建人： zhangmengxiong
 * 创建时间： 2016-7-14.
 * 联系方式: zmx_final@163.com
 */
class SambaScanTask(private val sambaServer: SambaServer, private val postExecute: AsyncPostExecute<ArrayList<SmbFile>>?) : AsyncTask<String, String, ArrayList<SmbFile>>() {

    override fun onPreExecute() {
        postExecute?.onPreExecute()
    }

    override fun onProgressUpdate(vararg values: String) {
        postExecute?.onProgressUpdate(values[0])
    }

    override fun doInBackground(vararg strings: String): ArrayList<SmbFile> {
        return SambaScanHelp.getFileList(sambaServer,strings[0])
    }

    override fun onPostExecute(s: ArrayList<SmbFile>?) {
        postExecute?.onPostExecute(s != null, s)
    }

    companion object {

        private val TAG = FindSDFileTask::class.java.simpleName
    }
}