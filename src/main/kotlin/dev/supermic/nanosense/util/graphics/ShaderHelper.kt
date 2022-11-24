package dev.supermic.nanosense.util.graphics

import dev.supermic.nanosense.NanoSenseMod
import dev.supermic.nanosense.event.AlwaysListening
import dev.supermic.nanosense.event.events.TickEvent
import dev.supermic.nanosense.event.events.render.ResolutionUpdateEvent
import dev.supermic.nanosense.event.listener
import dev.supermic.nanosense.util.Wrapper
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.shader.ShaderGroup
import net.minecraft.client.shader.ShaderLinkHelper
import net.minecraft.util.ResourceLocation

class ShaderHelper(shaderIn: ResourceLocation) : AlwaysListening {
    private val mc = Wrapper.minecraft

    val shader: ShaderGroup? =
        if (!OpenGlHelper.shadersSupported) {
            NanoSenseMod.logger.warn("Shaders are unsupported by OpenGL!")
            null
        } else {
            try {
                ShaderLinkHelper.setNewStaticShaderLinkHelper()

                ShaderGroup(mc.textureManager, mc.resourceManager, mc.framebuffer, shaderIn).also {
                    it.createBindFramebuffers(mc.displayWidth, mc.displayHeight)
                }
            } catch (e: Exception) {
                NanoSenseMod.logger.warn("Failed to load shaders")
                e.printStackTrace()

                null
            }
        }

    private var frameBuffersInitialized = false

    init {
        listener<TickEvent.Post> {
            if (!frameBuffersInitialized) {
                shader?.createBindFramebuffers(mc.displayWidth, mc.displayHeight)

                frameBuffersInitialized = true
            }
        }

        listener<ResolutionUpdateEvent> {
            shader?.createBindFramebuffers(it.width, it.height) // this will not run if on Intel GPU or unsupported Shaders
        }
    }

    fun getFrameBuffer(name: String) = shader?.getFramebufferRaw(name)
}