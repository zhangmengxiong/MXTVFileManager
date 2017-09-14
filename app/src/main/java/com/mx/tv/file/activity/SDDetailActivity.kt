package com.mx.tv.file.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.mx.tv.file.R
import com.mx.tv.file.base.BaseActivity
import com.mx.tv.file.base.MyApp
import com.mx.tv.file.models.FileTypeBean
import com.mx.tv.file.models.SDCardBean
import com.mx.tv.file.models.SDScanItem
import com.mx.tv.file.task.AsyncPostExecute
import com.mx.tv.file.task.FindSDFileTask
import com.mx.tv.file.utils.StringFormate
import kotlinx.android.synthetic.main.activity_sddetail.*

class SDDetailActivity : BaseActivity() {
    private lateinit var sdCardBean: SDCardBean
    private var findSDFileTask: FindSDFileTask? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sddetail)
        initView()
        initIntent()
    }

    override fun onResume() {
        super.onResume()
        initData()
    }

    private fun initData() {
        findSDFileTask?.cancel(true)
        findSDFileTask = FindSDFileTask(findSDFilePost)
        findSDFileTask?.executeOnExecutor(MyApp.executor, sdCardBean.getFile().absolutePath)
    }

    private fun initView() {
//        bottomLay.setPadding(MyApp.getScreenWidth() * 0.2.toInt(),
//                MyApp.getScreenWidth() * 0.2.toInt(),
//                MyApp.getScreenWidth() * 0.2.toInt(),
//                MyApp.getScreenWidth() * 0.2.toInt())

        pkgView.setFileType(FileTypeBean.APK, "安装包", R.drawable.apk_item_bg, R.drawable.ico_android)
        videoView.setFileType(FileTypeBean.VIDEO, "视频", R.drawable.video_item_bg, R.drawable.ico_video)
        picView.setFileType(FileTypeBean.PIC, "图片", R.drawable.pic_item_bg, R.drawable.ico_pic)
        musicView.setFileType(FileTypeBean.SOUND, "音乐", R.drawable.music_item_bg, R.drawable.ico_music)

        picView.setSubTitle(String.format(resources.getString(R.string.pic_size_str), "0"))
        videoView.setSubTitle(String.format(resources.getString(R.string.video_size_str), "0"))
        pkgView.setSubTitle(String.format(resources.getString(R.string.apk_size_str), "0"))
        musicView.setSubTitle(String.format(resources.getString(R.string.sound_size_str), "0"))
        textView.text = String.format(resources.getString(R.string.sum_size_str), "0")

        picView.setOnClickListener(itemClick)
        videoView.setOnClickListener(itemClick)
        pkgView.setOnClickListener(itemClick)
        musicView.setOnClickListener(itemClick)
        groupView.setOnClickListener(itemClick)

        progressBar.max = 1000000
        progressBar.progress = 0
    }

    private val findSDFilePost = object : AsyncPostExecute<String>() {
        override fun onProgressUpdate(value: Any?) {
            val item = value as SDScanItem?
            if (item != null) {
                Log.v(TAG, "" + item.TYPE + "  --> " + item.SIZE)
                when (item.TYPE) {
                    FileTypeBean.PIC -> picView.setSubTitle(String.format(resources.getString(R.string.pic_size_str), "" + item.SIZE))
                    FileTypeBean.VIDEO -> videoView.setSubTitle(String.format(resources.getString(R.string.video_size_str), "" + item.SIZE))
                    FileTypeBean.APK -> pkgView.setSubTitle(String.format(resources.getString(R.string.apk_size_str), "" + item.SIZE))
                    FileTypeBean.SOUND -> musicView.setSubTitle(String.format(resources.getString(R.string.sound_size_str), "" + item.SIZE))
                    FileTypeBean.UNKNOW -> textView.text = String.format(resources.getString(R.string.sum_size_str), "" + item.SIZE)
                }
            }
        }

        override fun onPreExecute() {
            progressBar2.visibility = View.VISIBLE
        }

        override fun onPostExecute(isOk: Boolean, result: String?) {
            progressBar2.visibility = View.INVISIBLE
        }

        override fun onCancelled() {
            progressBar2.visibility = View.INVISIBLE
        }
    }

    private fun initIntent() {
        val item = intent.getSerializableExtra("SDCardBean") as SDCardBean? ?: return

        sdCardBean = item
        titleTxv.text = sdCardBean.NAME
        sdCardBean.NAME = "全部文件"
        groupView.sdCardBean = sdCardBean
        val used = sdCardBean.getTotalSpace() - sdCardBean.getFreeSpace()
        try {
            val p = used / sdCardBean.getTotalSpace().toFloat()
            progressBar.progress = (p * progressBar.max).toInt()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        spaceTxv.text = String.format(resources.getString(R.string.has_use_str), StringFormate.fileSize2Show(used))
        totalTxv.text = String.format(resources.getString(R.string.max_size_str), StringFormate.fileSize2Show(sdCardBean.getTotalSpace()))
    }

    private val itemClick = View.OnClickListener { view ->
        var intent = Intent(this@SDDetailActivity, FileTypeListActivity::class.java)
        intent.putExtra("PATH", sdCardBean.getFile().absolutePath)
        when (view.id) {
            R.id.picView -> intent.putExtra("FileTypeBean", FileTypeBean.PIC)
            R.id.videoView -> intent.putExtra("FileTypeBean", FileTypeBean.VIDEO)
            R.id.pkgView -> intent.putExtra("FileTypeBean", FileTypeBean.APK)
            R.id.musicView -> intent.putExtra("FileTypeBean", FileTypeBean.SOUND)
            R.id.groupView -> {
                intent = Intent(this@SDDetailActivity, FileListActivity::class.java)
                intent.putExtra("PATH", sdCardBean.getFile().absolutePath)
            }
        }
        startActivity(intent)
    }

    override fun onDestroy() {
        findSDFileTask?.cancel(true)
        super.onDestroy()
    }

    companion object {
        private val TAG = SDDetailActivity::class.java.simpleName
    }
}
