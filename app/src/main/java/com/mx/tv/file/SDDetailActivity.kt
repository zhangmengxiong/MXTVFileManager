package com.mx.tv.file

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import com.mx.tv.file.models.FileTypeBean
import com.mx.tv.file.models.SDCardBean
import com.mx.tv.file.models.SDScanItem
import com.mx.tv.file.task.AsyncPostExecute
import com.mx.tv.file.task.FindSDFileTask
import com.mx.tv.file.utils.StringFormate
import com.mx.tv.file.utils.ViewUtils
import com.mx.tv.file.views.FileTypeCardView
import com.mx.tv.file.views.SDCardView


class SDDetailActivity : Activity() {

    @ViewUtils.ViewInject(R.id.progressBar)
    private val progressBar: ProgressBar? = null

    @ViewUtils.ViewInject(R.id.progressBar2)
    private val progressBar2: ProgressBar? = null

    @ViewUtils.ViewInject(R.id.title)
    private val title: TextView? = null

    @ViewUtils.ViewInject(R.id.textView)
    private val textView: TextView? = null

    @ViewUtils.ViewInject(R.id.groupView)
    private val groupView: SDCardView? = null

    @ViewUtils.ViewInject(R.id.pkgView)
    private val pkgView: FileTypeCardView? = null

    @ViewUtils.ViewInject(R.id.videoView)
    private val videoView: FileTypeCardView? = null

    @ViewUtils.ViewInject(R.id.picView)
    private val picView: FileTypeCardView? = null

    @ViewUtils.ViewInject(R.id.musicView)
    private val musicView: FileTypeCardView? = null

    @ViewUtils.ViewInject(R.id.spaceTxv)
    private val spaceTxv: TextView? = null

    @ViewUtils.ViewInject(R.id.totalTxv)
    private val totalTxv: TextView? = null

    private var sdCardBean: SDCardBean? = null
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
        if (sdCardBean != null) {
            if (findSDFileTask != null) findSDFileTask!!.cancel(true)
            findSDFileTask = FindSDFileTask(findSDFilePost)
            findSDFileTask!!.executeOnExecutor(MyApp.executor, sdCardBean!!.FILE!!.absolutePath)
        }
    }

    private fun initView() {
        ViewUtils.inject(this)
        pkgView!!.setFileType(FileTypeBean.APK, "安装包", R.drawable.apk_item_bg, R.drawable.ico_android)
        videoView!!.setFileType(FileTypeBean.VIDEO, "视频", R.drawable.video_item_bg, R.drawable.ico_video)
        picView!!.setFileType(FileTypeBean.PIC, "图片", R.drawable.pic_item_bg, R.drawable.ico_pic)
        musicView!!.setFileType(FileTypeBean.SOUND, "音乐", R.drawable.music_item_bg, R.drawable.ico_music)

        picView.setSubTitle(String.format(resources.getString(R.string.pic_size_str), "0"))
        videoView.setSubTitle(String.format(resources.getString(R.string.video_size_str), "0"))
        pkgView.setSubTitle(String.format(resources.getString(R.string.apk_size_str), "0"))
        musicView.setSubTitle(String.format(resources.getString(R.string.sound_size_str), "0"))
        textView!!.text = String.format(resources.getString(R.string.sum_size_str), "0")

        picView.setOnClickListener(itemClick)
        videoView.setOnClickListener(itemClick)
        pkgView.setOnClickListener(itemClick)
        musicView.setOnClickListener(itemClick)
        groupView!!.setOnClickListener(itemClick)

        progressBar!!.max = 1000000
        progressBar.progress = 0
    }

    private val findSDFilePost = object : AsyncPostExecute<String>() {
        override fun onProgressUpdate(value: Any?) {
            val item = value as SDScanItem?
            if (item != null) {
                Log.v(TAG, "" + item.TYPE + "  --> " + item.SIZE)
                when (item.TYPE) {
                    FileTypeBean.PIC -> picView!!.setSubTitle(String.format(resources.getString(R.string.pic_size_str), "" + item.SIZE))
                    FileTypeBean.VIDEO -> videoView!!.setSubTitle(String.format(resources.getString(R.string.video_size_str), "" + item.SIZE))
                    FileTypeBean.APK -> pkgView!!.setSubTitle(String.format(resources.getString(R.string.apk_size_str), "" + item.SIZE))
                    FileTypeBean.SOUND -> musicView!!.setSubTitle(String.format(resources.getString(R.string.sound_size_str), "" + item.SIZE))
                    FileTypeBean.UNKNOW -> textView!!.text = String.format(resources.getString(R.string.sum_size_str), "" + item.SIZE)
                }
            }
        }

        override fun onPreExecute() {
            progressBar2!!.visibility = View.VISIBLE
        }

        override fun onPostExecute(isOk: Boolean, result: String?) {
            progressBar2!!.visibility = View.INVISIBLE
        }

        override fun onCancelled() {
            progressBar2!!.visibility = View.INVISIBLE
        }
    }

    private fun initIntent() {
        sdCardBean = intent.getSerializableExtra("SDCardBean") as SDCardBean

        if (sdCardBean != null) {
            title!!.text = sdCardBean!!.NAME

            sdCardBean!!.NAME = "全部文件"
            groupView!!.sdCardBean = sdCardBean

            val used = sdCardBean!!.totalSpace - sdCardBean!!.freeSpace
            try {
                val p = used / sdCardBean!!.totalSpace.toFloat()
                progressBar!!.progress = (p * progressBar.max).toInt()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            spaceTxv!!.text = String.format(resources.getString(R.string.has_use_str), StringFormate.fileSize2Show(used))
            totalTxv!!.text = String.format(resources.getString(R.string.max_size_str), StringFormate.fileSize2Show(sdCardBean!!.totalSpace))
        }
    }

    private val itemClick = View.OnClickListener { view ->
        var intent = Intent(this@SDDetailActivity, FileTypeListActivity::class.java)
        intent.putExtra("PATH", sdCardBean!!.FILE!!.absolutePath)
        when (view.id) {
            R.id.picView -> intent.putExtra("FileTypeBean", FileTypeBean.PIC)
            R.id.videoView -> intent.putExtra("FileTypeBean", FileTypeBean.VIDEO)
            R.id.pkgView -> intent.putExtra("FileTypeBean", FileTypeBean.APK)
            R.id.musicView -> intent.putExtra("FileTypeBean", FileTypeBean.SOUND)
            R.id.groupView -> {
                intent = Intent(this@SDDetailActivity, FileListActivity::class.java)
                intent.putExtra("PATH", sdCardBean!!.FILE!!.absolutePath)
            }
        }
        startActivity(intent)
    }

    override fun onDestroy() {
        if (findSDFileTask != null) findSDFileTask!!.cancel(true)
        super.onDestroy()
    }

    companion object {
        private val TAG = SDDetailActivity::class.java.simpleName
    }
}
