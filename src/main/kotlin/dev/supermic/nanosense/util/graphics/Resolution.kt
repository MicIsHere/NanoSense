package dev.supermic.nanosense.util.graphics

import dev.supermic.nanosense.event.AlwaysListening
import dev.supermic.nanosense.module.modules.client.GuiSetting
import dev.supermic.nanosense.util.extension.fastCeil
import dev.supermic.nanosense.util.interfaces.Helper

object Resolution : AlwaysListening, Helper {
    val widthI
        get() = mc.displayWidth

    val heightI
        get() = mc.displayHeight

    val heightF
        get() = mc.displayHeight.toFloat()

    val widthF
        get() = mc.displayWidth.toFloat()

    val trollWidthF
        get() = widthF / GuiSetting.scaleFactorFloat

    val trollHeightF
        get() = heightF / GuiSetting.scaleFactorFloat

    val trollWidthI
        get() = trollWidthF.fastCeil()

    val trollHeightI
        get() = trollHeightF.fastCeil()
}