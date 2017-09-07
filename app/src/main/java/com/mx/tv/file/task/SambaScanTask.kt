package com.mx.tv.file.task

import android.os.AsyncTask
import com.mx.lib.samba.SambaScanHelp
import com.mx.lib.samba.SambaServer
import jcifs.smb.SmbAuthException
import jcifs.smb.SmbFile

/**
 * 创建人： zhangmengxiong
 * 创建时间： 2016-7-14.
 * 联系方式: zmx_final@163.com
 */
class SambaScanTask(private val sambaServer: SambaServer, private val postExecute: AsyncPostExecute<List<SmbFile>>?) : AsyncTask<String, String, List<SmbFile>>() {

    override fun onPreExecute() {
        postExecute?.onPreExecute()
    }

    override fun onProgressUpdate(vararg values: String) {
        postExecute?.onProgressUpdate(values[0])
    }

    override fun doInBackground(vararg strings: String): List<SmbFile> {
        try {
            return SambaScanHelp.getFileList(sambaServer, strings[0])
        } catch (e: SmbAuthException) {
            publishProgress("auth_error")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return emptyList()
    }

    override fun onPostExecute(s: List<SmbFile>?) {
        postExecute?.onPostExecute(s != null, s)
    }
}