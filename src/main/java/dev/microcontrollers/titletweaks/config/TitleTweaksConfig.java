package dev.microcontrollers.titletweaks.config;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.FloatSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class TitleTweaksConfig {
    public static final ConfigClassHandler<TitleTweaksConfig> CONFIG = ConfigClassHandler.createBuilder(TitleTweaksConfig.class)
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(FabricLoader.getInstance().getConfigDir().resolve("titletweaks.json"))
                    .build())
            .build();

    @SerialEntry public boolean disableTitles = false;
    @SerialEntry public float titleScale = 100F;
    @SerialEntry public boolean autoTitleScale = true;
    @SerialEntry public int titlePositionOffset = 0;
    @SerialEntry public int subtitlePositionOffset = 0;
    @SerialEntry public float titleOpacity = 100F;
    @SerialEntry public boolean removeTextShadow = false;
    @SerialEntry public boolean clearOnDisconnect = true;

    public static Screen configScreen(Screen parent) {
        return YetAnotherConfigLib.create(CONFIG, ((defaults, config, builder) -> builder
                .title(Text.translatable("title-tweaks.title-tweaks"))
                .category(ConfigCategory.createBuilder()
                        .name(Text.translatable("title-tweaks.title-tweaks"))
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.translatable("title-tweaks.disable-titles"))
                                .description(OptionDescription.of(Text.translatable("title-tweaks.disable-titles.description")))
                                .binding(defaults.disableTitles, () -> config.disableTitles, newVal -> config.disableTitles = newVal)
                                .controller(TickBoxControllerBuilder::create)
                                .build())
                        .option(Option.<Float>createBuilder()
                                .name(Text.translatable("title-tweaks.title-scale"))
                                .description(OptionDescription.of(Text.translatable("title-tweaks.title-scale.description")))
                                .binding(100F, () -> config.titleScale, newVal -> config.titleScale = newVal)
                                .controller(opt -> FloatSliderControllerBuilder.create(opt)
                                        .formatValue(value -> Text.of(String.format("%,.0f", value) + "%"))
                                        .range(0F, 100F)
                                        .step(1F))
                                .build())
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.translatable("title-tweaks.automatically-scale-titles"))
                                .description(OptionDescription.of(Text.translatable("title-tweaks.automatically-scale-titles.description")))
                                .binding(defaults.autoTitleScale, () -> config.autoTitleScale, newVal -> config.autoTitleScale = newVal)
                                .controller(TickBoxControllerBuilder::create)
                                .build())
                        .option(Option.<Integer>createBuilder()
                                .name(Text.translatable("title-tweaks.title-vertical-position"))
                                .description(OptionDescription.of(Text.translatable("title-tweaks.title-vertical-position.description")))
                                .binding(0, () -> config.titlePositionOffset, newVal -> config.titlePositionOffset = newVal)
                                .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                        .range(-100, 100)
                                        .step(1))
                                .build())
                        .option(Option.<Integer>createBuilder()
                                .name(Text.translatable("title-tweaks.subtitle-vertical-position"))
                                .description(OptionDescription.of(Text.translatable("title-tweaks.subtitle-vertical-position.description")))
                                .binding(0, () -> config.subtitlePositionOffset, newVal -> config.subtitlePositionOffset = newVal)
                                .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                        .range(-100, 100)
                                        .step(1))
                                .build())
                        .option(Option.<Float>createBuilder()
                                .name(Text.translatable("title-tweaks.title-opacity"))
                                .description(OptionDescription.of(Text.translatable("title-tweaks.title-opacity.description")))
                                .binding(100F, () -> config.titleOpacity, newVal -> config.titleOpacity = newVal)
                                .controller(opt -> FloatSliderControllerBuilder.create(opt)
                                        .formatValue(value -> Text.of(String.format("%,.0f", value) + "%"))
                                        .range(0F, 100F)
                                        .step(1F))
                                .build())
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.translatable("title-tweaks.remove-text-shadow"))
                                .description(OptionDescription.of(Text.translatable("title-tweaks.remove-text-shadow.description")))
                                .binding(defaults.removeTextShadow, () -> config.removeTextShadow, newVal -> config.removeTextShadow = newVal)
                                .controller(TickBoxControllerBuilder::create)
                                .build())
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.translatable("title-tweaks.clear-on-disconnect"))
                                .description(OptionDescription.of(Text.translatable("title-tweaks.clear-on-disconnect.description")))
                                .binding(defaults.clearOnDisconnect, () -> config.clearOnDisconnect, newVal -> config.clearOnDisconnect = newVal)
                                .controller(TickBoxControllerBuilder::create)
                                .build())
                        .build())
        )).generateScreen(parent);
    }

}