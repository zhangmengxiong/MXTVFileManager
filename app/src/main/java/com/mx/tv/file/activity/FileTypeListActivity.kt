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
import com.mx.tv.file.models.FileTypeBean
import com.mx.tv.file.task.AsyncPostExecute
import com.mx.tv.file.task.FileTypeScanTask
import com.mx.tv.file.utils.FileOpenUtil
import com.mx.tv.file.utils.ViewUtils
import java.io.File
import java.util.*

class FileTypeListActivity : BaseActivity() {
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
    private var fileTypeScanTask: FileTypeScanTask? = null
    private var rootDir: File? = null
    private var fileTypeBean: FileTypeBean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_type_list)

        initView()
        initData()
    }

    private fun initView() {
        ViewUtils.inject(this)
        listView!!.onItemClickListener = fileItemClick

    }

    private fun initData() {
        rootDir = File(intent.getStringExtra("PATH"))
        fileTypeBean = intent.getSerializableExtra("FileTypeBean") as FileTypeBean

        fileListAdapt = FileListAdapt(arrayList)
        listView!!.adapter = fileListAdapt

        curDirTxv!!.text = rootDir!!.absolutePath

        reGetFileList()
    }

    private fun reGetFileList() {
        if (fileTypeScanTask != null) fileTypeScanTask!!.cancel(true)
        arrayList.clear()
        fileListAdapt!!.notifyDataSetChanged()

        fileTypeScanTask = FileTypeScanTask(fileScanPost, rootDir!!.absolutePath, fileTypeBean!!)
        fileTypeScanTask!!.executeOnExecutor(MyApp.executor)

        title!!.text = rootDir!!.name
        if (arrayList.size > 0) {
            textView!!.text = String.format(resources.getString(R.string.list_index_size_str), "" + arrayList.size)
        } else {
            textView!!.text = String.format(resources.getString(R.string.list_index_size_str), "0")
        }
    }

    private val fileScanPost = object : AsyncPostExecute<ArrayList<FileBean>>() {
        override fun onPostExecute(isOk: Boolean, result: ArrayList<FileBean>?) {
            if (isOk) {
                arrayList.clear()
                arrayList.addAll(result!!)
                fileListAdapt!!.notifyDataSetChanged()

                emptyView!!.visibility = if (result.size <= 0) View.VISIBLE else View.GONE
                textView!!.text = String.format(resources.getString(R.string.sum_size_str), "" + arrayList.size)
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
            FileOpenUtil.openFile(this@FileTypeListActivity, bean.FILE!!)
        }
    }

    override fun onDestroy() {
        if (fileTypeScanTask != null) fileTypeScanTask!!.cancel(true)
        super.onDestroy()
    }
}
