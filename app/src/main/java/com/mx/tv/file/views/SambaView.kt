package com.mx.tv.file.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.mx.tv.file.R
import com.mx.lib.samba.SambaServer

/**
 * 创建人： zhangmengxiong
 * 创建时间： 2016-7-13.
 * 联系方式: zmx_final@163.com
 */
class SambaView : LinearLayout {
    private var icoImg: ImageView? = null
    private var name: TextView? = null
    private var size: TextView? = null
    var sambaServer: SambaServer? = null
        set(bean) {
            field = bean
            if (name != null) {
                name!!.text = sambaServer!!.serverName
            }
            if (size != null) {
//                val s = resources.getString(R.string.sd_size_formate)
//                size!!.text = String.format(s, StringFormate.fileSize2Show(sambaServer!!.freeSpace), StringFormate.fileSize2Show(sambaServer!!.totalSpace))
            }
        }

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    private fun init(context: Context) {
        LayoutInflater.from(context).inflate(R.layout.sd_item_lay, this, true)
        orientation = VERTICAL
        isFocusable = true
        setBackgroundResource(R.drawable.sd_item_bg)

        icoImg = findViewById<View>(R.id.icoImg) as ImageView
        name = findViewById<View>(R.id.name) as TextView
        size = findViewById<View>(R.id.size) as TextView

        if (sambaServer != null) {
            name!!.text = sambaServer!!.serverName
//            val s = resources.getString(R.string.sd_size_formate)
//            size!!.text = String.format(s, StringFormate.fileSize2Show(sambaServer!!.freeSpace), StringFormate.fileSize2Show(sambaServer!!.totalSpace))
        }
    }

    fun setIcon(res: Int) {
        if (icoImg != null) icoImg!!.setImageResource(res)
    }
}