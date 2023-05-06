package com.freelycar.demo.view.mark.model

import androidx.annotation.ColorInt

/**
 * Data class describing a color change
 */
internal data class ColorChange(
        @ColorInt val start: Int,
        @ColorInt val end: Int
)