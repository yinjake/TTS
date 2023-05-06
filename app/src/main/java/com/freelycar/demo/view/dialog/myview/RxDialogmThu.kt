package com.freelycar.demo.view.dialog.myview

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import com.airbnb.lottie.LottieAnimationView
import com.freelycar.demo.R
import com.freelycar.demo.view.dialog.RxDialog

class RxDialogmThu(context: Context?) : RxDialog(context!!) {
    val LottieView: LottieAnimationView

    init {
        val dialogView = LayoutInflater.from(context).inflate(
                R.layout.dialog_loading_thu, null)
        LottieView = dialogView.findViewById(R.id.animation_view)
        setContentView(dialogView)
        layoutParams!!.gravity = Gravity.CENTER
    }
}