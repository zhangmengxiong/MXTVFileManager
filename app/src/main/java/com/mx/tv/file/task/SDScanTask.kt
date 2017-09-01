package com.mx.tv.file.task


import android.os.AsyncTask

import com.mx.tv.file.models.SDCardBean
import com.mx.tv.file.utils.FileScanUtil

import java.util.ArrayList

/**
 * 扫描本地的SD卡
 * 创建人： zhangmengxiong
 * 创建时间： 2016-7-27.
 * 联系方式: zmx_final@163.com
 */
class SDScanTask(private val postExecute: AsyncPostExecute<ArrayList<SDCardBean>>?) : AsyncTask<String, String, ArrayList<SDCardBean>>() {

    override fun onPreExecute() {
        postExecute?.onPreExecute()
    }

    override fun doInBackground(vararg strings: String): ArrayList<SDCardBean> {
        return FileScanUtil.sdList
    }

    override fun onPostExecute(result: ArrayList<SDCardBean>?) {
        postExecute?.onPostExecute(result != null && result.size > 0, result)
    }
}
