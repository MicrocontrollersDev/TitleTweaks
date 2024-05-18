package dev.microcontrollers.titletweaks.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import dev.microcontrollers.titletweaks.config.TitleTweaksConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

/*
    The following marked methods were taken from Easeify under LGPLV3
    https://github.com/Polyfrost/Easeify/blob/main/LICENSE
    The code has been updated to 1.20 and with several fixes
 */
@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    @Shadow
    private Text title;
    @Shadow
    private Text subtitle;
    @Shadow public abstract TextRenderer getTextRenderer();
    @Unique
    //#if MC >= 1.20.6
    private final String methodTarget = "renderTitleAndSubtitle";
    //#else
    //$$ private final String methodTarget = "render";
    //#endif
    @Unique
    //#if MC >= 1.20.6
    private final int offset = 0;
    //#else
    //$$private final int offset = 1;
    //#endif

    // from Easeify
    @ModifyExpressionValue(method = methodTarget, at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;clamp(III)I"))
    private int disableTitles(int value) {
        if (TitleTweaksConfig.CONFIG.instance().disableTitles) return 0;
        else return value;
    }

    // from Easeify
    @Inject(method = methodTarget, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;scale(FFF)V", ordinal = 0, shift = At.Shift.AFTER))
    private void modifyTitleScale(DrawContext context, float tickDelta, CallbackInfo ci) {
        float titleScale = TitleTweaksConfig.CONFIG.instance().titleScale / 100;
        // this is a check for MCCIsland's server transition effect
        if (TitleTweaksConfig.CONFIG.instance().autoTitleScale && title.toString() != null && title.toString() != "literal{\uE000}[style={color=white,font=mcc:gui}]") {
            final float width = MinecraftClient.getInstance().textRenderer.getWidth(title) * 4.0F;
            if (width > context.getScaledWindowWidth()) {
                titleScale = (context.getScaledWindowWidth() / width) * TitleTweaksConfig.CONFIG.instance().titleScale / 100;
            }
        }
        context.getMatrices().scale(titleScale, titleScale, titleScale);
    }

    // from Easeify
    @Inject(method = methodTarget, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;scale(FFF)V", ordinal = 1, shift = At.Shift.AFTER))
    private void modifySubtitleScale(DrawContext context, float tickDelta, CallbackInfo ci) {
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

    @WrapWithCondition(method = methodTarget, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;III)I"), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;clamp(III)I"), to = @At(value = "TAIL")))
    private boolean cancelPreviousRender(DrawContext instance, TextRenderer textRenderer, Text text, int x, int y, int color) {
        return !TitleTweaksConfig.CONFIG.instance().removeTextShadow;
    }

    @ModifyArgs(method = methodTarget, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;III)I", ordinal = 0 + offset))
    private void cancelPreviousRenderTitle(Args args, @Share("x") LocalIntRef xRef, @Share("y") LocalIntRef yRef, @Share("color") LocalIntRef colorRef) {
        xRef.set(args.get(2));
        yRef.set(args.get(3));
        colorRef.set(args.get(4));
    }

    @Inject(method = methodTarget, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;III)I", ordinal = 0 + offset, shift = At.Shift.AFTER))
    private void renderWithoutShadowTitle(DrawContext context, float tickDelta, CallbackInfo ci, @Share("x") LocalIntRef xRef, @Share("y") LocalIntRef yRef, @Share("color") LocalIntRef colorRef) {
        if (TitleTweaksConfig.CONFIG.instance().removeTextShadow && title != null) context.drawText(getTextRenderer(), title, xRef.get(), yRef.get(), colorRef.get(), false);
    }

    @ModifyArgs(method = methodTarget, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;III)I", ordinal = 1 + offset))
    private void cancelPreviousRenderSubtitle(Args args, @Share("x1") LocalIntRef xRef1, @Share("y1") LocalIntRef yRef1, @Share("color1") LocalIntRef colorRef1) {
        xRef1.set(args.get(2));
        yRef1.set(args.get(3));
        colorRef1.set(args.get(4));
    }

    @Inject(method = methodTarget, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;III)I", ordinal = 1 + offset, shift = At.Shift.AFTER))
    private void renderWithoutShadowSubtitle(DrawContext context, float tickDelta, CallbackInfo ci, @Share("x1") LocalIntRef xRef1, @Share("y1") LocalIntRef yRef1, @Share("color1") LocalIntRef colorRef1) {
        if (TitleTweaksConfig.CONFIG.instance().removeTextShadow && subtitle != null) context.drawText(getTextRenderer(), subtitle, xRef1.get(), yRef1.get(), colorRef1.get(), false);
    }

}
