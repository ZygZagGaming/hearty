package io.github.zygzaggaming.hearty.mod;

import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

public class HeartyConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static ModConfigSpec.BooleanValue RENDER_DOUBLE_HEARTS = BUILDER.comment(" Whether or not hearts should store 1 hp instead of the vanilla 2").define("render_double_hearts", false);
    public static ModConfigSpec SPEC = BUILDER.build();

}
