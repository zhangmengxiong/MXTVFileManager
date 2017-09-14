package com.mx.tv.file.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView

import com.mx.tv.file.R
import com.mx.tv.file.models.FileTypeBean

/**
 * 创建人： zhangmengxiong
 * 创建时间： 2016-7-13.
 * 联系方式: zmx_final@163.com
 */
class FileTypeCardView : RelativeLayout {

    private var name: TextView? = null
    private var size: TextView? = null
    private var bgImg: ImageView? = null
    private var iconImg: ImageView? = null
    private var title: String? = null
    private var subTitle: String? = null
    private var bgRes: Int = 0
    private var iconRes: Int = 0
    private var fileTypeBean: FileTypeBean? = null

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
        LayoutInflater.from(context).inflate(R.layout.filetype_item_lay, this, true)
        isFocusable = true

        name = findViewById<View>(R.id.name) as TextView
        size = findViewById<View>(R.id.size) as TextView
        bgImg = findViewById<View>(R.id.bgImg) as ImageView
        iconImg = findViewById<View>(R.id.iconImg) as ImageView

        if (title != null) {
            name!!.text = title
        }
        if (subTitle != null) {
            size!!.text = subTitle
        }
        if (bgRes > 0) bgImg!!.setImageResource(bgRes)
        if (iconRes > 0) iconImg!!.setImageResource(iconRes)
    }

    fun setFileType(fileTypeBean: FileTypeBean, title: String, bg: Int, icon: Int) {
        this.fileTypeBean = fileTypeBean
        this.title = title
        bgRes = bg
        iconRes = icon
        if (name != null) {
            name!!.text = title
        }
        if (bgImg != null) bgImg!!.setImageResource(bgRes)
        if (iconImg != null) iconImg!!.setImageResource(iconRes)
    }

    fun setSubTitle(subTitle: String) {
        this.subTitle = subTitle
        if (size != null) {
            size!!.text = subTitle
        }
    }
}
