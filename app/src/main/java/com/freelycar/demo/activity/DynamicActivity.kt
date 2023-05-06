package com.freelycar.demo.activity

import android.annotation.SuppressLint
import android.graphics.PointF
import android.os.Bundle
import android.util.Log
import androidx.annotation.FloatRange
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.model.KeyPath
import com.freelycar.demo.databinding.DynamicActivityBinding
import com.freelycar.demo.util.MyLogUtils

private val COLORS = arrayOf(
    0xff5a5f,
    0x008489,
    0xa61d55
)
private val EXTRA_JUMP = arrayOf(0f, 20f, 50f)

class DynamicActivity : AppCompatActivity() {
    private var speed = 1
    private var colorIndex = 0
    private var extraJumpIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = DynamicActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.speedButton.setOnClickListener {
            speed = ++speed % 4
            updateButtonText(binding)
        }

        binding.colorButton.setOnClickListener {
            colorIndex = (colorIndex + 1) % COLORS.size
            updateButtonText(binding)
        }

        binding.jumpHeight.setOnClickListener {
            extraJumpIndex = (extraJumpIndex + 1) % EXTRA_JUMP.size
            updateButtonText(binding)
            setupValueCallbacks(binding)
        }

        binding.animationView.addLottieOnCompositionLoadedListener { _ ->
            binding.animationView.resolveKeyPath(KeyPath("**")).forEach {
                MyLogUtils.file(TAG, it.keysToString())
                setupValueCallbacks(binding)
            }
        }
        binding.animationView.setFailureListener { e ->
            Log.e(TAG, "Failed to load animation!", e)
        }

        updateButtonText(binding)
    }

    private fun setupValueCallbacks(binding: DynamicActivityBinding) {
        binding.animationView.addValueCallback(KeyPath("LeftArmWave"), LottieProperty.TIME_REMAP) { frameInfo ->
            2 * speed.toFloat() * frameInfo.overallProgress
        }

        val shirt = KeyPath("Shirt", "Group 5", "Fill 1")
        val leftArm = KeyPath("LeftArmWave", "LeftArm", "Group 6", "Fill 1")
        val rightArm = KeyPath("RightArm", "Group 6", "Fill 1")

        binding.animationView.addValueCallback(shirt, LottieProperty.COLOR) { COLORS[colorIndex] }
        binding.animationView.addValueCallback(leftArm, LottieProperty.COLOR) { COLORS[colorIndex] }
        binding.animationView.addValueCallback(rightArm, LottieProperty.COLOR) { COLORS[colorIndex] }
        val point = PointF()
        binding.animationView.addValueCallback(
            KeyPath("Body"),
            LottieProperty.TRANSFORM_POSITION
        ) { frameInfo ->
            val startX = frameInfo.startValue.x
            var startY = frameInfo.startValue.y
            var endY = frameInfo.endValue.y

            if (startY > endY) {
                startY += EXTRA_JUMP[extraJumpIndex]
            } else if (endY > startY) {
                endY += EXTRA_JUMP[extraJumpIndex]
            }
            point.set(startX, lerp(startY, endY, frameInfo.interpolatedKeyframeProgress))
            point
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateButtonText(binding: DynamicActivityBinding) {
        binding.speedButton.text = "Wave: ${speed}x Speed"
        binding.jumpHeight.text = "Extra jump height ${EXTRA_JUMP[extraJumpIndex]}"
    }

    fun lerp(a: Float, b: Float, @FloatRange(from = 0.0, to = 1.0) percentage: Float) = a + percentage * (b - a)

    companion object {
        val TAG = DynamicActivity::class.simpleName
    }
}
