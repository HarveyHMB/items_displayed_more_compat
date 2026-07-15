package com.harveyhmb.items_displayed_more_compat.mixin;

import net.fabricmc.fabric.impl.registry.sync.RegistrySyncManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RegistrySyncManager.class)
public class RegistrySyncManagerMixin {
    @Inject(at = @At("HEAD"), method = "/(configure|send)(Client|Packet)/", cancellable = true)
    private static void bypassRegistrySync(CallbackInfo info) {
        info.cancel();
    }
}