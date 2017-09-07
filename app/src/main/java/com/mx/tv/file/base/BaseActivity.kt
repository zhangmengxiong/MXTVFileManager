package com.mx.tv.file.base

import android.app.Activity
import android.widget.Toast

/**
 * 创建人： zhangmengxiong
 * 创建时间： 2017/9/7.
 * 联系方式: zmx_final@163.com
 */

open class BaseActivity : Activity() {

    protected fun showToast(s: String) {
        Toast.makeText(this@BaseActivity, s, Toast.LENGTH_SHORT).show()
    }

    protected fun showToast(res: Int) {
        Toast.makeText(this@BaseActivity, res, Toast.LENGTH_SHORT).show()
    }
}
