package com.freelycar.demo.view.dialog.myview

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.TextView
import com.airbnb.lottie.LottieAnimationView
import com.freelycar.demo.R
import com.freelycar.demo.view.dialog.RxDialog
import java.util.*

class RxDialogmMon(context: Context?) : RxDialog(context!!) {
    val reminderView: TextView
    val LottieView: LottieAnimationView
    private val loadingText = arrayOf("今天别忘记打卡哦！", "下班也不打卡,你来干嘛呀。", "你不打卡,老板怎么开工资呀！", "咦，你总有那么几天忘记打卡。", "今天又忘记打卡了呦。", "下班打卡，哇卡哇卡")

    init {
        val dialogView = LayoutInflater.from(context).inflate(
                R.layout.dialog_loading_mon, null)
        LottieView = dialogView.findViewById(R.id.animation_view)
        val random = Random()
        val number = Math.abs(random.nextInt() % loadingText.size)
        reminderView = dialogView.findViewById(R.id.tv_reminder)
        reminderView.text = loadingText[number]
        setContentView(dialogView)
        layoutParams!!.gravity = Gravity.CENTER
    }
}