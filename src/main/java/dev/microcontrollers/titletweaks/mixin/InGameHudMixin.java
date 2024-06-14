package dev.microcontrollers.titletweaks.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.microcontrollers.titletweaks.config.TitleTweaksConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/*
    The following marked methods were taken from Easeify under LGPLV3
    https://github.com/Polyfrost/Easeify/blob/main/LICENSE
    The code has been updated to 1.20 and with several fixes
 */
@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Shadow
    private Text title;
    @Shadow
    private Text subtitle;
    @Unique
    //#if MC >= 1.20.6
    private final String methodTarget = "renderTitleAndSubtitle";
    //#else
    //$$ private final String methodTarget = "render";
    //#endif
    @Unique
    //#if MC >= 1.21
    private final String textTarget = "Lnet/minecraft/client/gui/DrawContext;drawTextWithBackground(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;IIII)I";
    //#else
    //$$ private final String textTarget = "Lnet/minecraft/client/gui/DrawContext;drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;III)I";
    //#endif
    @Unique
    //#if MC >= 1.20.6
    private final int offset = 0;
    //#else
    //$$ private final int offset = 1;
    //#endif

    // from Easeify
    @ModifyExpressionValue(method = methodTarget, at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;clamp(III)I"))
    private int disableTitles(int value) {
        if (TitleTweaksConfig.CONFIG.instance().disableTitles) return 0;
        else return value;
    }

    // from Easeify
    @Inject(method = methodTarget, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;scale(FFF)V", ordinal = 0, shift = At.Shift.AFTER))
    //#if MC >= 1.21
    private void modifyTitleScale(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
    //#else
    //$$ private void modifyTitleScale(DrawContext context, float tickDelta, CallbackInfo ci) {
    //#endif
        float titleScale = TitleTweaksConfig.CONFIG.instance().titleScale / 100;
        // this is a check for MCCIsland's server transition effect
        if (TitleTweaksConfig.CONFIG.instance().autoTitleScale && title.toString() != null && !title.toString().equals("literal{\uE000}[style={color=white,font=mcc:gui}]")) {
            final float width = MinecraftClient.getInstance().textRenderer.getWidth(title) * 4.0F;
            if (width > context.getScaledWindowWidth()) {
                titleScale = (context.getScaledWindowWidth() / width) * TitleTweaksConfig.CONFIG.instance().titleScale / 100;
            }
        }
        context.getMatrices().scale(titleScale, titleScale, titleScale);
    }

    // from Easeify
    @Inject(method = methodTarget, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;scale(FFF)V", ordinal = 1, shift = At.Shift.AFTER))
    //#if MC >= 1.21
    private void modifySubtitleScale(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
    //#else
    //$$ private void modifySubtitleScale(DrawContext context, float tickDelta, CallbackInfo ci) {
    //#endif
        float titleScale = TitleTweaksConfig.CONFIG.instance().titleScale / 100;
        final float width = MinecraftClient.getInstance().textRenderer.getWidth(subtitle) * 2.0F;
        if (width > context.getScaledWindowWidth()) {
            titleScale = (context.getScaledWindowWidth() / width) * TitleTweaksConfig.CONFIG.instance().titleScale / 100;
        }
        context.getMatrices().scale(titleScale, titleScale, titleScale);
    }

    @ModifyArg(method = methodTarget, at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;clamp(III)I"), index = 2)
    private int modifyOpacity(int value) {
        return (int) (TitleTweaksConfig.CONFIG.instance().titleOpacity / 100 * 255);
    }

    @ModifyArg(method = methodTarget, at = @At(value = "INVOKE", target = textTarget, ordinal = offset), index = 3)
    private int modifyTitleVerticalPosition(int y) {
        return y - TitleTweaksConfig.CONFIG.instance().titlePositionOffset;
    }

    @ModifyArg(method = methodTarget, at = @At(value = "INVOKE", target = textTarget, ordinal = 1 + offset), index = 3)
    private int modifySubtitleVerticalPosition(int y) {
        return y - TitleTweaksConfig.CONFIG.instance().subtitlePositionOffset;
    }

    @WrapOperation(method = methodTarget, at = @At(value = "INVOKE", target = textTarget))
    //#if MC >= 1.21
    private int renderWithoutShadow(DrawContext instance, TextRenderer textRenderer, Text text, int x, int y, int width, int color, Operation<Integer> original, @Local(ordinal = 1) int k) {
    //#else
    //$$ private int renderWithoutShadow(DrawContext instance, TextRenderer textRenderer, Text text, int x, int y, int color, Operation<Integer> original) {
    //#endif
        if (TitleTweaksConfig.CONFIG.instance().removeTextShadow) {
            //#if MC >= 1.21
            instance.fill(x - 2, y - 2, x + width + 2, y + 9 + 2, k);
            //#endif
            return instance.drawText(textRenderer, text, x, y, color, false);
        }
        //#if MC >= 1.21
        return original.call(instance, textRenderer, text, x, y, width, color);
        //#else
        //$$ return original.call(instance, textRenderer, text, x, y, color);
        //#endif
    }

}
