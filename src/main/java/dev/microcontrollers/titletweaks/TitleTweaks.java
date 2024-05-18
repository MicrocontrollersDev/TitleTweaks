package dev.microcontrollers.titletweaks;

import dev.microcontrollers.titletweaks.config.TitleTweaksConfig;
import net.fabricmc.api.ModInitializer;

public class TitleTweaks implements ModInitializer {
	@Override
	public void onInitialize() {
		TitleTweaksConfig.CONFIG.load();
	}

}