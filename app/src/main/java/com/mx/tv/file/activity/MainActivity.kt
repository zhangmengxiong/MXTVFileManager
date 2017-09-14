package com.mx.tv.file.activity

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import com.mx.dlna.DeviceItem
import com.mx.lib.samba.SambaServer
import com.mx.tv.file.R
import com.mx.tv.file.base.BaseActivity
import com.mx.tv.file.base.MyApp
import com.mx.tv.file.models.SDCardBean
import com.mx.tv.file.task.AsyncPostExecute
import com.mx.tv.file.task.SDScanTask
import com.mx.tv.file.views.SDCardView
import com.mx.tv.file.views.SambaView
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

class MainActivity : BaseActivity() {
    private var sdScanTask: SDScanTask? = null
    private lateinit var firstLay: LinearLayout.LayoutParams
    private lateinit var addLay: LinearLayout.LayoutParams

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initData()

        val filter = IntentFilter(Intent.ACTION_MEDIA_MOUNTED)
        filter.addAction(Intent.ACTION_MEDIA_UNMOUNTED)
        filter.addDataScheme("file")
        registerReceiver(mReceiver, filter)
        EventBus.getDefault().register(this)
    }

    private fun initData() {
        if (haNoPermission()) return

        firstLay = LinearLayout.LayoutParams(
                (MyApp.getScreenWidth() * 0.25f).toInt(),
                (MyApp.getScreenHeight() * 0.6f).toInt())
        addLay = LinearLayout.LayoutParams(
                (MyApp.getScreenWidth() * 0.25f).toInt(),
                (MyApp.getScreenHeight() * 0.6f).toInt())
        addLay.leftMargin = MyApp.getScreenWidth() * 0.06f.toInt()

        sd_content.setPadding(
                (MyApp.getScreenWidth() * 0.05f).toInt(), 0,
                (MyApp.getScreenWidth() * 0.05f).toInt(), 0)

        if (sdScanTask != null) sdScanTask!!.cancel(true)
        sdScanTask = SDScanTask(sdScanPost)
        sdScanTask!!.executeOnExecutor(MyApp.executor)
    }

    private fun haNoPermission(): Boolean {
        if (Build.VERSION.SDK_INT >= 23) {
            //减少是否拥有权限
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                //弹出对话框接收权限
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 11)
                return true
            }
        }
        return false
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //TODO:已授权
            initData()
        } else {
            //TODO:用户拒绝
        }
    }

    private val sdScanPost = object : AsyncPostExecute<ArrayList<SDCardBean>>() {
        override fun onPostExecute(isOk: Boolean, result: ArrayList<SDCardBean>?) {
            if (isOk) {
                result?.forEach {
                    val view = SDCardView(this@MainActivity)
                    view.setOnClickListener(sdOnclick)
                    view.sdCardBean = it
                    addContentItem(view)
                }

                val fmt = resources.getString(R.string.sub_title_str)

                subTitle.text = String.format(fmt, "" + result?.size)
            }
            progressBar.visibility = View.GONE
        }

        override fun onPreExecute() {
            progressBar.visibility = View.VISIBLE
        }
    }

    private val mReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (action == Intent.ACTION_MEDIA_MOUNTED) {
                initData()
            } else if (action == Intent.ACTION_MEDIA_UNMOUNTED) {
                initData()
            }
        }
    }

    private val sdOnclick = View.OnClickListener { view ->
        val bean = (view as SDCardView).sdCardBean
        if (bean != null) {
            val intent = Intent(this@MainActivity, SDDetailActivity::class.java)
            intent.putExtra("SDCardBean", bean)
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        if (sdScanTask != null) sdScanTask!!.cancel(true)
        unregisterReceiver(mReceiver)
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    private val smbClick: View.OnClickListener = View.OnClickListener { v ->
        if (v !is SambaView) return@OnClickListener
        val sambaServer = v.sambaServer ?: return@OnClickListener

        val intent = Intent(this@MainActivity, SambaActivity::class.java)
        intent.putExtra("SambaServer", sambaServer)
        startActivity(intent)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onReciveSamba(sambaServer: SambaServer) {
        showToast("找到Samba服务器：" + sambaServer.serverName)

        val view = SambaView(this@MainActivity)
        view.setOnClickListener(smbClick)
        view.sambaServer = sambaServer
        addContentItem(view)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onReciveDLNA(deviceItem: DeviceItem) {
        showToast("找到DLNA服务器：" + deviceItem.label)
    }

    private fun addContentItem(view: View) {
        val param = if (sd_content.childCount <= 0) firstLay else addLay

        if (view is SambaView) {
            sd_content.addView(view, param)
            view.requestFocus()
        } else {
            sd_content.addView(view, 0, param)
        }
    }
}
