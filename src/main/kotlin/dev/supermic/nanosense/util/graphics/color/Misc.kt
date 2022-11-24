package dev.supermic.nanosense.util.graphics.color

import net.minecraft.client.renderer.GlStateManager

fun ColorRGB.setGLColor() {
    GlStateManager.color(rFloat, gFloat, bFloat, aFloat)
}