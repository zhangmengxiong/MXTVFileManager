package com.mx.tv.file.task

/**
 * 异步回调函数
 *
 * @param <T>
</T> */
abstract class AsyncPostExecute<T> {
    open fun onPostExecute(isOk: Boolean, result: T?) {}

    open fun onPreExecute() {}

    open fun onCancelled() {}

    open fun onProgressUpdate(value: Any?) {}
}
