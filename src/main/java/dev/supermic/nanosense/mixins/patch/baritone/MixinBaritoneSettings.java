package dev.supermic.nanosense.mixins.patch.baritone;

import baritone.api.Settings;
import dev.supermic.nanosense.event.events.baritone.BaritoneSettingsInitEvent;
import dev.supermic.nanosense.event.events.baritone.BaritoneSettingsInitEvent;
import dev.supermic.nanosense.util.BaritoneUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Settings.class, remap = false)
public class MixinBaritoneSettings {
    @Inject(method = "<init>", at = @At("RETURN"))
    private void baritoneSettingsInit(CallbackInfo ci) {
        BaritoneUtils.INSTANCE.setInitialized(true);
        BaritoneSettingsInitEvent.INSTANCE.post();
    }
}
