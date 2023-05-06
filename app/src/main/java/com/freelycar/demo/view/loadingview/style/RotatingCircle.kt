package com.freelycar.demo.view.loadingview.style

import android.animation.ValueAnimator
import com.freelycar.demo.view.loadingview.animation.SpriteAnimatorBuilder
import com.freelycar.demo.view.loadingview.sprite.CircleSprite

/**
 * @author tamsiree
 */
class RotatingCircle : CircleSprite() {
    override fun onCreateAnimation(): ValueAnimator? {
        val fractions = floatArrayOf(0f, 0.5f, 1f)
        return SpriteAnimatorBuilder(this).rotateX(fractions, 0, -180, -180).rotateY(fractions, 0, 0, -180).duration(1200).easeInOut(*fractions)
                .build()
    }
}