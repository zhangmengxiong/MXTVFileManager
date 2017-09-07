package com.mx.tv.file.activity

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.TextView
import com.mx.tv.file.R
import com.mx.tv.file.adapts.FileListAdapt
import com.mx.tv.file.base.BaseActivity
import com.mx.tv.file.base.MyApp
import com.mx.tv.file.models.FileBean
import com.mx.tv.file.task.AsyncPostExecute
import com.mx.tv.file.task.FileScanTask
import com.mx.tv.file.utils.FileOpenUtil
import com.mx.tv.file.utils.ViewUtils
import java.io.File
import java.util.*

class FileListActivity : BaseActivity() {
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

    private val arrayList = ArrayList<FileBean>()
    private var fileListAdapt: FileListAdapt? = null
    private var fileScanTask: FileScanTask? = null
    private var rootDir: File? = null
    private var curDir: File? = null
    private var listIndexStr: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_list)
        initView()
        initData()
    }

    private fun initView() {
        ViewUtils.inject(this)
        listView!!.onItemClickListener = fileItemClick
        listIndexStr = resources.getString(R.string.list_index_size_str)
    }

    private fun initData() {
        rootDir = File(intent.getStringExtra("PATH"))
        curDir = rootDir

        fileListAdapt = FileListAdapt(arrayList)
        listView!!.adapter = fileListAdapt

        reGetFileList()
    }

    private fun reGetFileList() {
        if (curDir != null) {
            if (fileScanTask != null) fileScanTask!!.cancel(true)
            arrayList.clear()
            fileListAdapt!!.notifyDataSetChanged()

            fileScanTask = FileScanTask(fileScanPost)
            fileScanTask!!.executeOnExecutor(MyApp.executor, curDir!!.absolutePath)

            curDirTxv!!.text = curDir!!.absolutePath
            title!!.text = curDir!!.name
            textView!!.text = String.format(listIndexStr!!, arrayList.size)
        }
    }

    private val fileScanPost = object : AsyncPostExecute<ArrayList<FileBean>>() {
        override fun onPostExecute(isOk: Boolean, result: ArrayList<FileBean>?) {
            if (isOk) {
                arrayList.clear()
                arrayList.addAll(result!!)
                fileListAdapt!!.notifyDataSetChanged()

                emptyView!!.visibility = if (result.size <= 0) View.VISIBLE else View.GONE
                textView!!.text = String.format(resources.getString(R.string.list_index_size_str), "" + arrayList.size)
            }

            loadingLay!!.visibility = View.GONE
        }

        override fun onPreExecute() {
            emptyView!!.visibility = View.GONE
            loadingLay!!.visibility = View.VISIBLE
        }
    }

    private val fileItemClick = AdapterView.OnItemClickListener { adapterView, view, i, l ->
        val bean = fileListAdapt!!.getItem(i)
        if (bean != null) {
            if (bean.isDirectory) {
                curDir = bean.FILE
                reGetFileList()
            } else {
                FileOpenUtil.openFile(this@FileListActivity, bean.FILE!!)
            }
        }
    }

    override fun onBackPressed() {
        if (curDir == rootDir)
            super.onBackPressed()
        else {
            curDir = curDir!!.parentFile
            reGetFileList()
        }
    }

    override fun onDestroy() {
        if (fileScanTask != null) fileScanTask!!.cancel(true)
        super.onDestroy()
    }
}
