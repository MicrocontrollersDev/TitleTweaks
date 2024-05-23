package dev.microcontrollers.titletweaks.mixin;

import dev.microcontrollers.titletweaks.config.TitleTweaksConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Shadow @Final public InGameHud inGameHud;

    //#if MC >= 1.20.6
    @Inject(method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;Z)V", at = @At("HEAD"))
    private void clearTitles(Screen disconnectionScreen, boolean transferring, CallbackInfo ci) {
    //#else
    //$$ @Inject(method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V", at = @At("HEAD"))
    //$$ private void clearTitles(Screen disconnectionScreen, CallbackInfo ci) {
    //#endif
        if (TitleTweaksConfig.CONFIG.instance().clearOnDisconnect) inGameHud.clearTitle();
    }
}
