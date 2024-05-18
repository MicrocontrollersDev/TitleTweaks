package dev.microcontrollers.titletweaks.config;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.FloatSliderControllerBuilder;
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
    @SerialEntry public float titleOpacity = 100F;
    @SerialEntry public boolean removeTextShadow = false;

    @SuppressWarnings("deprecation")
    public static Screen configScreen(Screen parent) {
        return YetAnotherConfigLib.create(CONFIG, ((defaults, config, builder) -> builder
                .title(Text.literal("Title Tweaks"))
                .category(ConfigCategory.createBuilder()
                        .name(Text.literal("Title Tweaks"))
                        .option(Option.createBuilder(boolean.class)
                                .name(Text.literal("Disable Titles"))
                                .description(OptionDescription.of(Text.of("Remove titles entirely.")))
                                .binding(defaults.disableTitles, () -> config.disableTitles, newVal -> config.disableTitles = newVal)
                                .controller(TickBoxControllerBuilder::create)
                                .build())
                        .option(Option.createBuilder(float.class)
                                .name(Text.literal("Title Scale"))
                                .description(OptionDescription.of(Text.of("Set the scale for titles.")))
                                .binding(100F, () -> config.titleScale, newVal -> config.titleScale = newVal)
                                .controller(opt -> FloatSliderControllerBuilder.create(opt)
                                        .valueFormatter(value -> Text.of(String.format("%,.0f", value) + "%"))
                                        .range(0F, 100F)
                                        .step(1F))
                                .build())
                        .option(Option.createBuilder(boolean.class)
                                .name(Text.literal("Automatically Scale Titles"))
                                .description(OptionDescription.of(Text.of("Scale titles automatically if they go past the edges of your screen.")))
                                .binding(defaults.autoTitleScale, () -> config.autoTitleScale, newVal -> config.autoTitleScale = newVal)
                                .controller(TickBoxControllerBuilder::create)
                                .build())
                        .option(Option.createBuilder(float.class)
                                .name(Text.literal("Title Opacity"))
                                .description(OptionDescription.of(Text.of("Set the opacity for titles.")))
                                .binding(100F, () -> config.titleOpacity, newVal -> config.titleOpacity = newVal)
                                .controller(opt -> FloatSliderControllerBuilder.create(opt)
                                        .valueFormatter(value -> Text.of(String.format("%,.0f", value) + "%"))
                                        .range(0F, 100F)
                                        .step(1F))
                                .build())
                        .option(Option.createBuilder(boolean.class)
                                .name(Text.literal("Remove Text Shadow"))
                                .description(OptionDescription.of(Text.of("Removes the text shadow from titles.")))
                                .binding(defaults.removeTextShadow, () -> config.removeTextShadow, newVal -> config.removeTextShadow = newVal)
                                .controller(TickBoxControllerBuilder::create)
                                .build())
                        .build())
        )).generateScreen(parent);
    }

}