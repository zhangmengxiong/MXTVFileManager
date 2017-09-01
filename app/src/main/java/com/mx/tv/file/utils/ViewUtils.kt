package com.mx.tv.file.utils

import android.app.Activity
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.reflect.Field
import java.lang.reflect.Method

/**
 * Created by zmx_f on 2016-6-23.
 */
object ViewUtils {
    private val METHOD_SET_CONTENTVIEW = "setContentView"

    fun inject(activity: Activity) {
        injectContentView(activity)
        injectViews(activity)
    }

    /**
     * 注入主布局文件
     *
     * @param activity
     */
    private fun injectContentView(activity: Activity) {
        val clazz = activity.javaClass
        // 查询类上是否存在ContentView注解
        val contentView = clazz.getAnnotation(ContentView::class.java)
        if (contentView != null)
        // 存在
        {
            val contentViewLayoutId = contentView.value
            try {
                val method = clazz.getMethod(METHOD_SET_CONTENTVIEW,
                        Int::class.javaPrimitiveType)
                method.isAccessible = true
                method.invoke(activity, contentViewLayoutId)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    private val METHOD_FIND_VIEW_BY_ID = "findViewById"

    /**
     * 注入所有的控件
     *
     * @param activity
     */
    private fun injectViews(activity: Activity) {
        val clazz = activity.javaClass
        val fields = clazz.declaredFields
        // 遍历所有成员变量
        for (field in fields) {

            val viewInjectAnnotation = field
                    .getAnnotation(ViewInject::class.java)
            if (viewInjectAnnotation != null) {
                val viewId = viewInjectAnnotation.value
                if (viewId != -1) {
                    // 初始化View
                    try {
                        val method = clazz.getMethod(METHOD_FIND_VIEW_BY_ID,
                                Int::class.javaPrimitiveType)
                        val resView = method.invoke(activity, viewId)
                        field.isAccessible = true
                        field.set(activity, resView)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
            }

        }

    }

    @Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
    @Retention(RetentionPolicy.RUNTIME)
    annotation class ContentView(val value: Int)

    @Target(AnnotationTarget.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    annotation class ViewInject(val value: Int)
}
