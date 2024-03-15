package io.github.zygzaggaming.hearty.mod;

import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

public class HeartyConfig {
    public static ModConfigSpec.BooleanValue RENDER_DOUBLE_HEARTS;
    public static void register() {
        ModConfigSpec.Builder clientBuilder = new ModConfigSpec.Builder();
        RENDER_DOUBLE_HEARTS = clientBuilder.comment(" Whether or not hearts should store 1 hp instead of the vanilla 2").define("render_double_hearts", false);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, clientBuilder.build());
    }
}
