package com.freelycar.demo.view.colorpicker.builder

import com.freelycar.demo.view.colorpicker.ColorPickerView.WHEEL_TYPE
import com.freelycar.demo.view.colorpicker.renderer.ColorWheelRenderer
import com.freelycar.demo.view.colorpicker.renderer.FlowerColorWheelRenderer
import com.freelycar.demo.view.colorpicker.renderer.SimpleColorWheelRenderer

/**
 * @author tamsiree
 * @date 2018/6/11 11:36:40 整合修改
 */
object ColorWheelRendererBuilder {
    @JvmStatic
    fun getRenderer(wheelType: WHEEL_TYPE?): ColorWheelRenderer {
        when (wheelType) {
            WHEEL_TYPE.CIRCLE -> return SimpleColorWheelRenderer()
            WHEEL_TYPE.FLOWER -> return FlowerColorWheelRenderer()
            else -> {
            }
        }
        throw IllegalArgumentException("wrong WHEEL_TYPE")
    }
}