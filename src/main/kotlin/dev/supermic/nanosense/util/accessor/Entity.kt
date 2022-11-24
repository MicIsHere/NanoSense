package dev.supermic.nanosense.util.accessor

import net.minecraft.entity.EntityLivingBase

fun EntityLivingBase.onItemUseFinish() {
    (this as dev.supermic.nanosense.mixins.accessor.entity.AccessorEntityLivingBase).trollInvokeOnItemUseFinish()
}