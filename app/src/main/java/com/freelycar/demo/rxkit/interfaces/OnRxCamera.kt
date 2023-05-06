package com.freelycar.demo.rxkit.interfaces

import java.io.File

/**
 *
 * @author Tamsiree
 * @date 2017/8/9
 */
interface OnRxCamera {
    fun onBefore()
    fun onSuccessCompress(filePhoto: File?)
    fun onSuccessExif(filePhoto: File?)
}