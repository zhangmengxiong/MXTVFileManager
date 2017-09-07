package com.mx.tv.file.adapts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.mx.tv.file.R
import com.mx.tv.file.models.FileTypeBean
import com.mx.tv.file.utils.StringFormate
import jcifs.smb.SmbFile
import java.text.SimpleDateFormat
import java.util.*

/**
 * 创建人： zhangmengxiong
 * 创建时间： 2016-7-14.
 * 联系方式: zmx_final@163.com
 */
class SambaListAdapt(private var arrayList: ArrayList<SmbFile>) : BaseAdapter() {
    private val simpleDateFormat: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:ss")

    override fun getCount(): Int {
        return arrayList.size
    }

    override fun getItem(i: Int): SmbFile {
        return arrayList[i]
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    override fun getView(i: Int, view: View?, viewGroup: ViewGroup): View {
        var view = view
        val holder: ViewHolder
        val bean = getItem(i)
        if (view == null) {
            view = LayoutInflater.from(viewGroup.context).inflate(R.layout.file_list_item, viewGroup, false)
            holder = ViewHolder()
            holder.icon = view!!.findViewById<View>(R.id.icon) as ImageView
            holder.name = view.findViewById<View>(R.id.name) as TextView
            holder.subName = view.findViewById<View>(R.id.subName) as TextView
            holder.time = view.findViewById<View>(R.id.time) as TextView
            view.tag = holder
        } else {
            holder = view.tag as ViewHolder
        }

        holder.name!!.text = bean.name
        holder.time!!.text = simpleDateFormat.format(Date(bean.lastModified()))
        holder.icon!!.setImageResource(R.drawable.ico_folder)

        if (bean.isDirectory) {
            holder.name!!.text = bean.name.dropLast(1)
            holder.subName!!.text = String.format(viewGroup.resources.getString(R.string.sum_size_str), bean.childSize())
            holder.icon!!.setImageResource(R.drawable.ico_folder)
        } else {
            holder.subName!!.text = StringFormate.fileSize2Show(bean.length())
            when (FileTypeBean.fromName(bean.name)) {
                FileTypeBean.APK -> holder.icon!!.setImageResource(R.drawable.i_apk)
                FileTypeBean.VIDEO -> holder.icon!!.setImageResource(R.drawable.i_video)
                FileTypeBean.PIC -> holder.icon!!.setImageResource(R.drawable.i_pic)
                FileTypeBean.SOUND -> holder.icon!!.setImageResource(R.drawable.i_music)
                else -> holder.icon!!.setImageResource(R.drawable.i_default)
            }
        }
        return view
    }

    internal inner class ViewHolder {
        var icon: ImageView? = null
        var name: TextView? = null
        var subName: TextView? = null
        var time: TextView? = null
    }
}

private fun SmbFile.childSize(): Int {
    return try {
        0
    } catch (e: Exception) {
        0
    }
}
