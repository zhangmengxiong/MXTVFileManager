package com.mx.tv.file.activity

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import com.mx.tv.file.R
import com.mx.tv.file.adapts.FileListAdapt
import com.mx.tv.file.base.BaseActivity
import com.mx.tv.file.base.MyApp
import com.mx.tv.file.task.AsyncPostExecute
import com.mx.tv.file.task.FileScanTask
import com.mx.tv.file.utils.FileOpenUtil
import com.mx.tv.file.utils.ViewUtils
import kotlinx.android.synthetic.main.activity_file_list.*
import java.io.File
import java.util.*
import kotlin.collections.HashMap

class FileListActivity : BaseActivity() {
    private val historyMap = HashMap<String, Int>()
    private val arrayList = ArrayList<File>()
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
        listView.onItemClickListener = fileItemClick
        listIndexStr = resources.getString(R.string.list_index_size_str)
    }

    private fun initData() {
        rootDir = File(intent.getStringExtra("PATH"))
        curDir = rootDir

        fileListAdapt = FileListAdapt(arrayList)
        listView.adapter = fileListAdapt

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
            titleTxv.text = curDir!!.name
            textView.text = String.format(listIndexStr!!, arrayList.size)
        }
    }

    private val fileScanPost = object : AsyncPostExecute<ArrayList<File>>() {
        override fun onPostExecute(isOk: Boolean, result: ArrayList<File>?) {
            if (isOk) {
                arrayList.clear()
                arrayList.addAll(result!!)
                fileListAdapt!!.notifyDataSetChanged()

                emptyView.visibility = if (result.size <= 0) View.VISIBLE else View.GONE
                textView.text = String.format(resources.getString(R.string.list_index_size_str), "" + arrayList.size)

                val p = historyMap[curDir?.absolutePath]
                if (p != null && p > 0) {
                    listView?.setSelection(p)
                }
            }

            loadingLay.visibility = View.GONE
        }

        override fun onPreExecute() {
            emptyView.visibility = View.GONE
            loadingLay.visibility = View.VISIBLE
        }
    }

    private val fileItemClick = AdapterView.OnItemClickListener { _, _, i, _ ->
        val bean = fileListAdapt!!.getItem(i) as File?
        if (bean != null) {
            if (bean.isDirectory) {
                historyMap.put(curDir!!.absolutePath, i)

                curDir = bean
                reGetFileList()
            } else {
                FileOpenUtil.openFile(this@FileListActivity, bean)
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
