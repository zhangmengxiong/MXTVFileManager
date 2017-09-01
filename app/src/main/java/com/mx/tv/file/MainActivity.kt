package com.mx.tv.file

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.mx.tv.file.models.SDCardBean
import com.mx.tv.file.task.AsyncPostExecute
import com.mx.tv.file.task.SDScanTask
import com.mx.tv.file.views.SDCardView
import java.util.*

class MainActivity : Activity() {
    private var sd_content: LinearLayout? = null
    private var subTitle: TextView? = null
    private var progressBar: ProgressBar? = null
    private var sdScanTask: SDScanTask? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()

        val filter = IntentFilter(Intent.ACTION_MEDIA_MOUNTED)
        filter.addAction(Intent.ACTION_MEDIA_UNMOUNTED)
        filter.addDataScheme("file")
        registerReceiver(mReceiver, filter)
    }

    override fun onStart() {
        super.onStart()
    }

    private fun initView() {
        progressBar = findViewById<View>(R.id.progressBar) as ProgressBar
        sd_content = findViewById<View>(R.id.sd_content) as LinearLayout
        subTitle = findViewById<View>(R.id.subTitle) as TextView
        sd_content!!.post { initData() }
    }

    private fun initData() {
        if (sdScanTask != null) sdScanTask!!.cancel(true)
        sdScanTask = SDScanTask(sdScanPost)
        sdScanTask!!.executeOnExecutor(MyApp.executor)
    }

    private val sdScanPost = object : AsyncPostExecute<ArrayList<SDCardBean>>() {
        override fun onPostExecute(isOk: Boolean, result: ArrayList<SDCardBean>?) {
            if (isOk) {
                for (i in result!!.indices) {
                    val view = SDCardView(this@MainActivity)
                    view.setOnClickListener(sdOnclick)

                    view.sdCardBean = result[i]
                    sd_content!!.addView(view, sd_content!!.height * 280 / 396, LinearLayout.LayoutParams.MATCH_PARENT)

                    if (i > 0) {
                        val layoutParams = view.layoutParams as LinearLayout.LayoutParams
                        layoutParams.setMargins(30, 0, 0, 0)
                        view.layoutParams = layoutParams
                    } else {
                        view.requestFocus()
                    }
                }

                val fmt = resources.getString(R.string.sub_title_str)

                subTitle!!.text = String.format(fmt, "" + result.size)
            }
            progressBar!!.visibility = View.GONE
        }

        override fun onPreExecute() {
            sd_content!!.removeAllViews()
            progressBar!!.visibility = View.VISIBLE
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
        super.onDestroy()
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }
}
