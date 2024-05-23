package com.schuanhe.auto_redbook

import timber.log.Timber
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FileLoggingTree() : Timber.DebugTree() {

    var log =  getLogFile()
    var log2 = getLogFile2()
    private lateinit var logFile: File
     override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
         logFile = if ("AutoRedBook" == tag){
             log
         }else{
             log2
         }
        try {
            val writer = FileWriter(logFile, true)
            val timeStamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault()).format(Date())
            writer.append(timeStamp)
                .append(" ")
                .append(tag)
                .append(": ")
                .append(message)
                .append('\n')
            writer.flush()
            writer.close()
        } catch (e: IOException) {
            Timber.tag("FileLoggingTree").e("文件写入失败: %s", e.message)
        }

         super.log(priority, tag, message, t)
    }

    private fun getLogFile(): File {
        val date = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
        val logDir = File("/storage/emulated/0/AutoRedBook/")
        if (!logDir.exists()) {
            logDir.mkdirs()
        }
        return File(logDir, "$date.log")
    }

    private fun getLogFile2(): File {
        val date = SimpleDateFormat("yyyyMMddHH", Locale.getDefault()).format(Date())
        val logDir = File("/storage/emulated/0/AutoRedBook/xx/")
        if (!logDir.exists()) {
            logDir.mkdirs()
        }
        return File(logDir, "$date.log")
    }

}
