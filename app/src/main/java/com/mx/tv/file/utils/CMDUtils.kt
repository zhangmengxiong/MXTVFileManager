package com.mx.tv.file.utils

import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader

/**
 * 创建人： zhangmengxiong
 * 创建时间： 2016-7-13.
 * 联系方式: zmx_final@163.com
 */
object CMDUtils {
    private val COMMAND_SH = "sh"
    private val COMMAND_SU = "su hanyastar123"
    private val COMMAND_LINE_END = "\n"
    private val COMMAND_EXIT = "exit\n"

    /**
     * 执行一条cmd命令，得到返回结果
     *
     * @param cmd    命令
     * @param needSU 是否需要root权限来执行
     * @return 返回执行命令的结果
     */
    fun execCMD(cmd: String, needSU: Boolean): String {
        var cmd = cmd
        var result = ""
        val process: Process
        try {
            if (needSU) {
                cmd = COMMAND_SU + COMMAND_LINE_END + cmd
            }

            process = Runtime.getRuntime().exec(cmd)
            val input = InputStreamReader(process.inputStream)
            val buffer = BufferedReader(input)
            var tmp = buffer.readLine()

            while (tmp != null) {
                result = result + "\r\n" + tmp
                tmp = buffer.readLine()
            }
            result = result.trim { it <= ' ' }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return result
    }

    fun execCMD(commands: Array<String>, needSU: Boolean): String {
        var results = ""
        var process: Process? = null
        var successReader: BufferedReader? = null
        var errorReader: BufferedReader? = null
        var errorMsg: StringBuilder? = null

        var dos: DataOutputStream? = null
        try {
            if (needSU)
                process = Runtime.getRuntime().exec(COMMAND_SU)
            else
                process = Runtime.getRuntime().exec(COMMAND_SH)

            dos = DataOutputStream(process!!.outputStream)
            for (command in commands) {
                if (command == null) {
                    continue
                }
                dos.write(command.toByteArray())
                dos.writeBytes(COMMAND_LINE_END)
                dos.flush()
            }
            dos.writeBytes(COMMAND_EXIT)
            dos.flush()

            process.waitFor()
            errorMsg = StringBuilder()
            successReader = BufferedReader(InputStreamReader(
                    process.inputStream))
            errorReader = BufferedReader(InputStreamReader(
                    process.errorStream))
            var lineStr: String? = successReader.readLine()
            while (lineStr != null) {
                results = results + "\r\n" + lineStr
                lineStr = successReader.readLine()
            }
            lineStr = errorReader.readLine()
            while (lineStr != null) {
                errorMsg.append(lineStr)
                lineStr = errorReader.readLine()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                if (dos != null) {
                    dos.close()
                }
                if (successReader != null) {
                    successReader.close()
                }
                if (errorReader != null) {
                    errorReader.close()
                }
                if (process != null) {
                    process.destroy()
                }
            } catch (ignored: Exception) {
            }

        }
        return results
    }

    fun execCMD(commands: Array<String>, needSU: Boolean, resultCall: ResultCall?): String {
        var process: Process? = null
        var successReader: BufferedReader? = null
        var errorReader: BufferedReader? = null
        var errorMsg: StringBuilder? = null

        var dos: DataOutputStream? = null
        try {
            if (needSU)
                process = Runtime.getRuntime().exec(COMMAND_SU)
            else
                process = Runtime.getRuntime().exec(COMMAND_SH)

            dos = DataOutputStream(process!!.outputStream)
            for (command in commands) {
                if (command == null) {
                    continue
                }
                dos.write(command.toByteArray())
                dos.writeBytes(COMMAND_LINE_END)
                dos.flush()
            }
            dos.writeBytes(COMMAND_EXIT)
            dos.flush()

            process.waitFor()
            errorMsg = StringBuilder()
            successReader = BufferedReader(InputStreamReader(
                    process.inputStream))
            errorReader = BufferedReader(InputStreamReader(
                    process.errorStream))
            var lineStr: String? = successReader.readLine()
            while (lineStr != null) {
                resultCall?.newLine(lineStr)
                lineStr = successReader.readLine()
            }
            lineStr = successReader.readLine()
            while (lineStr != null) {
                errorMsg.append(lineStr)
                lineStr = successReader.readLine()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                if (dos != null) {
                    dos.close()
                }
                if (successReader != null) {
                    successReader.close()
                }
                if (errorReader != null) {
                    errorReader.close()
                }
                if (process != null) {
                    process.destroy()
                }
            } catch (ignored: Exception) {
            }

        }
        return ""
    }

    interface ResultCall {
        fun newLine(s: String)
    }
}
