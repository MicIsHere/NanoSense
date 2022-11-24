package dev.supermic.nanosense.module.modules.render

import dev.supermic.nanosense.event.events.TickEvent
import dev.supermic.nanosense.event.listener
import dev.supermic.nanosense.module.Category
import dev.supermic.nanosense.module.Module
import dev.supermic.nanosense.util.Bind
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

internal object Fov : Module(
    name = "Fov",
    category = Category.RENDER,
    description = "Configures FOV",
    visible = false
) {
    private val fovValue by setting("Fov", 120.0f, 1.0f..180.0f, 0.5f)
    private val dynamicFov by setting("Dynamic Fov", false)
    private val zoomBind: Bind by setting("Zoom Bind", Bind(), {
        if (isEnabled && switchZoom && it) {
            zooming = !zooming
            updateSmoothCamera()
        }
    })
    private val switchZoom by setting("Switch Zoom", false, { !zoomBind.isEmpty })
    private val zoomFov by setting("Zoom Fov", 40.0f, 1.0f..180.0f, 0.5f, { !zoomBind.isEmpty })
    private val sensitivityMultiplier by setting("Sensitivity Multiplier", 1.0f, 0.1f..2.0f, 0.1f, { !zoomBind.isEmpty })
    private val smoothCamera by setting("Smooth Camera", false, { !zoomBind.isEmpty })

    private var zooming = false

    override fun getHudInfo(): String {
        return "%.1f".format(fovValue)
    }

    init {
        onDisable {
            zooming = false
            mc.gameSettings.smoothCamera = false
        }

        listener<TickEvent.Pre> {
            if (zooming && smoothCamera) {
                mc.gameSettings.smoothCamera = true
            }

            if (!switchZoom) {
                zooming = zoomBind.isDown()
                updateSmoothCamera()
            }
        }
    }

    private fun updateSmoothCamera() {
        if (smoothCamera) {
            mc.gameSettings.smoothCamera = zooming
        }
    }

    @JvmStatic
    fun getFOVModifierDynamicFov(value: Float): Float {
        return if (isEnabled && dynamicFov) {
            getFov()
        } else {
            value
        }
    }

    @JvmStatic
    fun getFOVModifierNoDynamicFov(cir: CallbackInfoReturnable<Float>) {
        if (isEnabled && !dynamicFov) {
            cir.returnValue = getFov()
        }
    }

    @JvmStatic
    fun getMouseSensitivity(value: Float): Float {
        return if (isEnabled && zooming) {
            mc.gameSettings.mouseSensitivity * 0.6f * sensitivityMultiplier + 0.2f
        } else {
            value
        }
    }

    private fun getFov(): Float {
        return if (!zooming) {
            fovValue
        } else {
            zoomFov
        }
    }
}