/*
Copyright 2014 David Morrissey

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package com.freelycar.demo.view.scaleimage

import android.graphics.PointF
import java.io.Serializable

/**
 * @author tamsiree
 * Wraps the scale, center and orientation of a displayed image for easy restoration on screen rotate.
 */
class ImageViewState(val scale: Float, center: PointF, orientation: Int) : Serializable {
    private val centerX: Float = center.x
    private val centerY: Float = center.y
    val orientation: Int

    val center: PointF
        get() = PointF(centerX, centerY)

    init {
        this.orientation = orientation
    }
}