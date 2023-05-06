package com.freelycar.demo.view.dialog.myview

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.TextView
import com.airbnb.lottie.LottieAnimationView
import com.freelycar.demo.R
import com.freelycar.demo.view.dialog.RxDialog
import java.io.*
import java.util.*

class RxDialogAcfunVideoLoading(context: Context?) : RxDialog(context!!) {
    //val loadingBar: ProgressBar
    val reminderView: TextView
    val LottieView: LottieAnimationView
    private val loadingText = arrayOf("今天别忘记打卡哦！", "上班不打卡嘛？。", "你不打卡,老板怎么开工资哈！", "你总有那么几天忘记打卡，诶。", "今天又忘记打卡了。", "打卡打卡，哇卡。")
    //private val loadingText = arrayOf("hello! xiao yin ")

    init {
       // var uri = "android.resource://" + getPackageName().toString() + "/" + R.raw.funny_smile
        val dialogView = LayoutInflater.from(context).inflate(
                R.layout.dialog_loading_progress_acfun_video, null)
        //loadingBar = dialogView.findViewById(R.id.loading_progressBar)
        LottieView = dialogView.findViewById(R.id.animation_view)
        val random = Random()
        val number = Math.abs(random.nextInt() % loadingText.size)
        reminderView = dialogView.findViewById(R.id.tv_reminder)
        reminderView.text = loadingText[number]
        setContentView(dialogView)
        layoutParams!!.gravity = Gravity.CENTER
  //      MyLogUtils.file("99", ":  "+context?.let { assetFilePath(it,"chinese-flag.json") })
  //      LottieView.setAnimation(context?.let { assetFilePath(it,"chinese-flag") })
//        LottieView.setAnimation(context?.let { AssetsFileUtil.getFromRaw(context)})
    }

    /**
     * 读取Raw文件中的内容
     */
    fun readRaw(context: Context, rawId: Int): String {
        return BufferedReader(InputStreamReader(context.resources.openRawResource(rawId))).use {
            val sb = StringBuilder()
            it.forEachLine { s ->
                sb.append(s)
            }
            sb.toString()
        }
    }


    fun assetFilePath(context: Context, asset: String): String {
        val file = File(context.filesDir, asset)
        try {
            val inpStream: InputStream = context.assets.open(asset)
            try {
                val outStream = FileOutputStream(file, false)
                val buffer = ByteArray(4 * 1024)
                var read: Int

                while (true) {
                    read = inpStream.read(buffer)
                    if (read == -1) {
                        break
                    }
                    outStream.write(buffer, 0, read)
                }
                outStream.flush()
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
            return file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }
}