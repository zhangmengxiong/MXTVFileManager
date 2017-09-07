package com.mx.tv.file.activity

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.TextView
import com.mx.tv.file.R
import com.mx.tv.file.adapts.SambaListAdapt
import com.mx.tv.file.base.BaseActivity
import com.mx.tv.file.base.MyApp
import com.mx.lib.samba.SambaServer
import com.mx.tv.file.task.AsyncPostExecute
import com.mx.tv.file.task.SambaScanTask
import com.mx.tv.file.utils.FileOpenUtil
import com.mx.tv.file.utils.ViewUtils
import jcifs.smb.SmbFile
import java.io.File
import java.util.*

class SambaActivity : BaseActivity() {
    @ViewUtils.ViewInject(R.id.listView)
    private val listView: ListView? = null

    @ViewUtils.ViewInject(R.id.title)
    private val title: TextView? = null

    @ViewUtils.ViewInject(R.id.curDirTxv)
    private val curDirTxv: TextView? = null

    @ViewUtils.ViewInject(R.id.textView)
    private val textView: TextView? = null

    @ViewUtils.ViewInject(R.id.emptyView)
    private val emptyView: View? = null

    @ViewUtils.ViewInject(R.id.loadingLay)
    private val loadingLay: View? = null

    private val arrayList = ArrayList<SmbFile>()
    private var sambaListAdapt: SambaListAdapt? = null
    private var sambaScanTask: SambaScanTask? = null
    private lateinit var sambaServer: SambaServer
    private var curDir = File("/")
    private var listIndexStr: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_samba)
        initView()
        initData()
    }

    private fun initView() {
        ViewUtils.inject(this)
        listView!!.onItemClickListener = fileItemClick
        listIndexStr = resources.getString(R.string.list_index_size_str)
    }

    private fun initData() {
        sambaServer = intent.getSerializableExtra("SambaServer") as SambaServer

        sambaListAdapt = SambaListAdapt(arrayList)
        listView!!.adapter = sambaListAdapt

        reGetFileList()
    }

    private fun reGetFileList() {
        if (sambaScanTask != null) sambaScanTask!!.cancel(true)
        arrayList.clear()
        sambaListAdapt!!.notifyDataSetChanged()

        sambaScanTask = SambaScanTask(sambaServer, fileScanPost)
        sambaScanTask!!.executeOnExecutor(MyApp.executor, curDir.absolutePath)

        curDirTxv!!.text = curDir.absolutePath
        title!!.text = sambaServer.serverName
        textView!!.text = String.format(listIndexStr!!, arrayList.size)
    }

    private val fileScanPost = object : AsyncPostExecute<List<SmbFile>>() {
        override fun onPostExecute(isOk: Boolean, result: List<SmbFile>?) {
            if (isOk) {
                arrayList.clear()
                arrayList.addAll(result!!)
                sambaListAdapt!!.notifyDataSetChanged()

                emptyView!!.visibility = if (result.isEmpty()) View.VISIBLE else View.GONE
                textView!!.text = String.format(resources.getString(R.string.list_index_size_str), "" + arrayList.size)
            }

            loadingLay!!.visibility = View.GONE
        }

        override fun onProgressUpdate(value: Any?) {
            if (value == "auth_error") {
                showToast("需要验证账号密码")
            }
        }

        override fun onPreExecute() {
            emptyView!!.visibility = View.GONE
            loadingLay!!.visibility = View.VISIBLE
        }
    }

    private val fileItemClick = AdapterView.OnItemClickListener { _, _, i, _ ->
        val bean = sambaListAdapt!!.getItem(i) as SmbFile?
        if (bean != null) {
            if (bean.isDirectory) {
                curDir = File(bean.canon)
                reGetFileList()
            } else {
                FileOpenUtil.openFile(this@SambaActivity, "http://localhost:10001/" + bean.path!!)
            }
        }
    }

    override fun onBackPressed() {
        if (curDir.absolutePath == "/")
            super.onBackPressed()
        else {
            curDir = curDir.parentFile
            reGetFileList()
        }
    }

    override fun onDestroy() {
        if (sambaScanTask != null) sambaScanTask!!.cancel(true)
        super.onDestroy()
    }
}
